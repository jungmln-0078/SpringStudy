package example.mybatis.blog.controller;

import example.mybatis.blog.aspect.Authentication;
import example.mybatis.blog.model.Member;
import example.mybatis.blog.model.MemberDTO;
import example.mybatis.blog.model.MemberLoginDTO;
import example.mybatis.blog.module.JwtManager;
import example.mybatis.blog.response.ResponseDTO;
import example.mybatis.blog.service.MemberService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static example.mybatis.blog.module.APIHelper.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Validated
public class MemberController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtManager jwtManager;

    @ApiOperation(value = "회원 리스트 조회", notes = "모든 회원을 조회합니다.", responseReference = "ResponseDTO<List<Member>>")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<List<Member>>> getMembers() {
        return setResponseData(HttpStatus.OK, memberService.getMembers(),null);
    }

    @ApiOperation(value = "회원 등록", notes = "회원을 등록합니다.", responseReference = "ResponseDTO<String>")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> postMember(@RequestBody @Valid MemberDTO memberDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return setResponseData(HttpStatus.BAD_REQUEST, parseErrors(bindingResult), "회원 등록에 실패하였습니다.");
            }
            BigInteger newMember = memberService.addMember(memberDTO);
            String jwt = jwtManager.createToken(newMember.longValue());
            return setResponseHeaderJwt(setResponseData(HttpStatus.CREATED, "http://localhost:8080/api/member/" + newMember, null), jwt);
        } catch (DataAccessException e) {
            return e.getCause().getClass().equals(SQLIntegrityConstraintViolationException.class) ?
                    setResponseData(HttpStatus.BAD_REQUEST, null, "회원 등록에 실패하였습니다. (이메일이 중복되었습니다.)") :
                    setResponseData(HttpStatus.BAD_REQUEST, null, "회원 등록에 실패하였습니다.");
        }
    }

    @ApiOperation(value = "회원 로그인", notes = "이메일과 비밀번호로 로그인합니다.", responseReference = "ResponseDTO<String>")
    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> login(@RequestBody @Valid MemberLoginDTO memberLoginDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return setResponseData(HttpStatus.BAD_REQUEST, parseErrors(bindingResult), "로그인에 실패하였습니다.");
            }
            Member member = memberService.getMemberByPw(memberLoginDTO.getEmail(), memberLoginDTO.getPassword());
            String jwt = jwtManager.createToken(member.getMid());
            return setResponseHeaderJwt(setResponseData(HttpStatus.OK, "http://localhost:8080/api/member/" + member.getMid(), null), jwt);
        } catch (Exception e) {
            return setResponseData(HttpStatus.UNAUTHORIZED, null, "로그인에 실패하였습니다.");
        }
    }

    @Authentication
    @ApiOperation(value = "회원 수정", notes = "회원을 수정합니다.", responseReference = "ResponseDTO<String>")
    @PutMapping(value = "{mid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> putMember(HttpServletRequest request, @PathVariable @Valid @Positive long mid, @RequestBody @Valid MemberDTO memberDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return setResponseData(HttpStatus.BAD_REQUEST, parseErrors(bindingResult), "회원 수정에 실패하였습니다.");
            }
            boolean isUpdated = memberService.updateMember(mid, memberDTO) == 1;
            return isUpdated ?
                    setResponseData(HttpStatus.OK, "http://localhost:8080/api/member/" + mid, "수정 되었습니다.") :
                    setResponseData(HttpStatus.NOT_FOUND, null, "회원 수정에 실패하였습니다. (해당 회원을 찾을 수 없습니다.)");
        } catch (DataAccessException e) {
            return setResponseData(HttpStatus.BAD_REQUEST, null, "회원 수정에 실패하였습니다.");
        }
    }

    @Authentication
    @ApiOperation(value = "회원 삭제", notes = "회원을 삭제합니다.", responseReference = "ResponseDTO<String>")
    @DeleteMapping(value = "{mid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> deleteMember(HttpServletRequest request, @PathVariable @Valid @Positive long mid) {
        try {
            boolean isDeleted = memberService.deleteMember(mid) == 1;
            return isDeleted ?
                    setResponseData(HttpStatus.OK, null, "성공적으로 삭제되었습니다.") :
                    setResponseData(HttpStatus.NOT_FOUND, null, "회원 삭제에 실패하였습니다. (해당 회원을 찾을 수 없습니다.)");
        } catch (DataAccessException e) {
            return setResponseData(HttpStatus.BAD_REQUEST, null, "회원 삭제에 실패하였습니다.");
        }
    }
}
