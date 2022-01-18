package example.mybatis.blog.controller;

import example.mybatis.blog.aspect.Authentication;
import example.mybatis.blog.model.ReplyDTO;
import example.mybatis.blog.model.ReplyWriteDTO;
import example.mybatis.blog.module.JwtManager;
import example.mybatis.blog.response.ResponseDTO;
import example.mybatis.blog.service.ReplyService;
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

import static example.mybatis.blog.module.APIHelper.parseErrors;
import static example.mybatis.blog.module.APIHelper.setResponseData;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
@Validated
public class ReplyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ReplyService replyService;

    @Autowired
    private JwtManager jwtManager;

    @Authentication
    @ApiOperation(value = "댓글 작성", notes = "댓글을 작성합니다.", responseReference = "ResponseDTO<String>")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> addReply(HttpServletRequest request, @RequestBody @Valid ReplyWriteDTO replyWriteDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return setResponseData(HttpStatus.BAD_REQUEST, parseErrors(bindingResult), "댓글 작성에 실패하였습니다.");
            }
            ReplyDTO replyDTO = new ReplyDTO(replyWriteDTO.getAid(), jwtManager.getJwtMid(request.getHeader("jwt")), replyWriteDTO.getContent());
            BigInteger newReply = replyService.addReply(replyDTO);
            return setResponseData(HttpStatus.CREATED, "", null);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return setResponseData(HttpStatus.BAD_REQUEST, null, "댓글 작성에 실패하였습니다.");
        }
    }

    @Authentication
    @ApiOperation(value = "댓글 수정", notes = "댓글을 수정합니다.", responseReference = "ResponseDTO<String>")
    @PutMapping(value = "{rid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> updateReply(HttpServletRequest request, @PathVariable @Valid @Positive long rid, @RequestBody String content) {
        try {
            boolean isUpdated = replyService.updateReply(rid, content) == 1;
            return isUpdated ?
                    setResponseData(HttpStatus.OK, "", null) :
                    setResponseData(HttpStatus.NOT_FOUND, null, "댓글 수정에 실패하였습니다.");
        } catch (DataAccessException e) {
            return setResponseData(HttpStatus.BAD_REQUEST, null, "댓글 수정에 실패하였습니다.");
        }
    }

    @Authentication
    @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제합니다.", responseReference = "ResponseDTO<String>")
    @DeleteMapping(value = "{rid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> deleteReply(HttpServletRequest request, @PathVariable @Valid @Positive long rid) {
        try {
            boolean isDeleted = replyService.deleteReply(rid) == 1;
            return isDeleted ?
                    setResponseData(HttpStatus.OK, null, "성공적으로 삭제되었습니다.") :
                    setResponseData(HttpStatus.NOT_FOUND, null, "댓글 삭제에 실패하였습니다.");
        } catch (DataAccessException e) {
            return setResponseData(HttpStatus.BAD_REQUEST, null, "댓글 삭제에 실패하였습니다.");
        }
    }
}
