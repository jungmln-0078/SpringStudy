package example.mybatis.blog.controller;

import example.mybatis.blog.aspect.Authentication;
import example.mybatis.blog.model.Member;
import example.mybatis.blog.model.MemberDTO;
import example.mybatis.blog.model.MemberLoginDTO;
import example.mybatis.blog.module.JwtManager;
import example.mybatis.blog.response.ResponseBuilder;
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
        List<Member> members = memberService.getMembers();
        return new ResponseBuilder<List<Member>>()
                .setStatus(HttpStatus.OK)
                .setBody(members, String.valueOf(members.size()))
                .build();
    }

    @ApiOperation(value = "회원 등록", notes = "회원을 등록합니다.", responseReference = "ResponseDTO<String>")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> postMember(@RequestBody @Valid MemberDTO memberDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseBuilder<String>()
                        .setStatus(HttpStatus.BAD_REQUEST)
                        .setBody(parseErrors(bindingResult), "회원 등록에 실패하였습니다.")
                        .build();
            }
            BigInteger newMember = memberService.addMember(memberDTO);
            String jwt = jwtManager.createToken(newMember.longValue());
            return new ResponseBuilder<String>()
                    .setStatus(HttpStatus.CREATED)
                    .setHeader("jwt", jwt)
                    .setBody(null, "성공적으로 생성되었습니다.")
                    .build();
        } catch (DataAccessException e) {
            return e.getCause().getClass().equals(SQLIntegrityConstraintViolationException.class) ?
                    new ResponseBuilder<String>()
                            .setStatus(HttpStatus.BAD_REQUEST)
                            .setBody(null, "회원 등록에 실패하였습니다. (이메일이 중복되었습니다.)")
                            .build() :
                    new ResponseBuilder<String>()
                            .setStatus(HttpStatus.BAD_REQUEST)
                            .setBody(null, "회원 등록에 실패하였습니다.")
                            .build();
        }
    }

    @ApiOperation(value = "회원 로그인", notes = "이메일과 비밀번호로 로그인합니다.", responseReference = "ResponseDTO<String>")
    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> login(@RequestBody @Valid MemberLoginDTO memberLoginDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseBuilder<String>()
                        .setStatus(HttpStatus.BAD_REQUEST)
                        .setBody(parseErrors(bindingResult), "로그인에 실패하였습니다.")
                        .build();
            }
            Member member = memberService.getMemberByPw(memberLoginDTO.getEmail(), memberLoginDTO.getPassword());
            String jwt = jwtManager.createToken(member.getMid());
            return new ResponseBuilder<String>()
                    .setStatus(HttpStatus.OK)
                    .setHeader("jwt", jwt)
                    .setBody(null, "로그인 되었습니다.")
                    .build();
        } catch (Exception e) {
            return new ResponseBuilder<String>()
                    .setStatus(HttpStatus.UNAUTHORIZED)
                    .setBody(null, "로그인에 실패하였습니다.")
                    .build();
        }
    }

    @Authentication
    @ApiOperation(value = "회원 수정", notes = "회원을 수정합니다.", responseReference = "ResponseDTO<String>")
    @PutMapping(value = "{mid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> putMember(HttpServletRequest request, @PathVariable @Valid @Positive long mid, @RequestBody @Valid MemberDTO memberDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseBuilder<String>()
                        .setStatus(HttpStatus.BAD_REQUEST)
                        .setBody(parseErrors(bindingResult), "회원 수정에 실패하였습니다.")
                        .build();
            }
            boolean isUpdated = memberService.updateMember(mid, memberDTO) == 1;
            return isUpdated ?
                    new ResponseBuilder<String>()
                            .setStatus(HttpStatus.OK)
                            .setBody(null, "성공적으로 수정되었습니다.")
                            .build() :
                    new ResponseBuilder<String>()
                            .setStatus(HttpStatus.NOT_FOUND)
                            .setBody(null, "회원 수정에 실패하였습니다. (해당 회원을 찾을 수 없습니다.)")
                            .build();
        } catch (DataAccessException e) {
            return new ResponseBuilder<String>()
                    .setStatus(HttpStatus.BAD_REQUEST)
                    .setBody(null, "회원 수정에 실패하였습니다.")
                    .build();
        }
    }

    @Authentication
    @ApiOperation(value = "회원 삭제", notes = "회원을 삭제합니다.", responseReference = "ResponseDTO<String>")
    @DeleteMapping(value = "{mid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> deleteMember(HttpServletRequest request, @PathVariable @Valid @Positive long mid) {
        try {
            boolean isDeleted = memberService.deleteMember(mid) == 1;
            return isDeleted ?
                    new ResponseBuilder<String>()
                            .setStatus(HttpStatus.OK)
                            .setBody(null, "성공적으로 삭제되었습니다.")
                            .build() :
                    new ResponseBuilder<String>()
                            .setStatus(HttpStatus.NOT_FOUND)
                            .setBody(null, "회원 삭제에 실패하였습니다. (해당 회원을 찾을 수 없습니다.)")
                            .build();
        } catch (DataAccessException e) {
            return new ResponseBuilder<String>()
                    .setStatus(HttpStatus.BAD_REQUEST)
                    .setBody(null, "회원 삭제에 실패하였습니다.")
                    .build();
        }
    }
}
