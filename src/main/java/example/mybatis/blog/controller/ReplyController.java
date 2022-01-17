package example.mybatis.blog.controller;

import example.mybatis.blog.model.ReplyDTO;
import example.mybatis.blog.module.JwtManager;
import example.mybatis.blog.response.ResponseDTO;
import example.mybatis.blog.service.ReplyService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigInteger;

import static example.mybatis.blog.module.APIHelper.parseErrors;
import static example.mybatis.blog.module.APIHelper.setResponseData;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @Autowired
    private JwtManager jwtManager;

    @ApiOperation(value = "댓글 작성", notes = "댓글을 작성합니다.", responseReference = "ResponseDTO<String>")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> writeReply(HttpServletRequest request, @RequestBody @Valid ReplyDTO replyDTO, BindingResult bindingResult) {
        try {
            if (!jwtManager.checkClaim(request.getHeader("jwt"))) {
                return setResponseData(HttpStatus.UNAUTHORIZED, null, "인증되지 않은 요청입니다.");
            }
            if (bindingResult.hasErrors()) {
                return setResponseData(HttpStatus.BAD_REQUEST, parseErrors(bindingResult), "댓글 작성에 실패하였습니다.");
            }
            BigInteger newReply = replyService.addReply(replyDTO);
            return setResponseData(HttpStatus.CREATED, "", null);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return setResponseData(HttpStatus.BAD_REQUEST, null, "댓글 작성에 실패하였습니다.");
        }
    }

    @ApiOperation(value = "댓글 수정", notes = "댓글을 수정합니다.", responseReference =  "ResponseDTO<String>")
    @PutMapping(value = "{rid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> updateReply(HttpServletRequest request, @PathVariable long rid, @RequestBody String content) {
        try {
            if (!jwtManager.checkClaim(request.getHeader("jwt"))) {
                return setResponseData(HttpStatus.UNAUTHORIZED, null, "인증되지 않은 요청입니다.");
            }
            boolean isUpdated = replyService.updateReply(rid, content) == 1;
            return isUpdated ?
                    setResponseData(HttpStatus.OK, "", null) :
                    setResponseData(HttpStatus.NOT_FOUND, null, "댓글 수정에 실패하였습니다.");
        } catch (DataAccessException e) {
            return setResponseData(HttpStatus.BAD_REQUEST, null, "댓글 수정에 실패하였습니다.");
        }
    }

    @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제합니다.", responseReference = "ResponseDTO<String>")
    @DeleteMapping(value = "{rid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> deleteReply(HttpServletRequest request, @PathVariable long rid) {
        try {
            if (!jwtManager.checkClaim(request.getHeader("jwt"))) {
                return setResponseData(HttpStatus.UNAUTHORIZED, null, "인증되지 않은 요청입니다.");
            }
            boolean isDeleted = replyService.deleteReply(rid) == 1;
            return isDeleted ?
                    setResponseData(HttpStatus.OK, null, "성공적으로 삭제되었습니다.") :
                    setResponseData(HttpStatus.NOT_FOUND, null, "댓글 삭제에 실패하였습니다.");
        } catch (DataAccessException e) {
            return setResponseData(HttpStatus.BAD_REQUEST, null, "댓글 삭제에 실패하였습니다.");
        }
    }
}
