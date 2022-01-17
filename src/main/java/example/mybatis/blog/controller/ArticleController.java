package example.mybatis.blog.controller;

import example.mybatis.blog.model.Article;
import example.mybatis.blog.model.ArticleDTO;
import example.mybatis.blog.module.JwtManager;
import example.mybatis.blog.response.ResponseDTO;
import example.mybatis.blog.service.ArticleService;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static example.mybatis.blog.module.APIHelper.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
@Validated
public class ArticleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private JwtManager jwtManager;

    @ApiOperation(value = "게시글 리스트 조회", notes = "게시글을 조회합니다.", responseReference = "ResponseDTO<List<Article>>")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<List<Article>>> getArticles(HttpServletResponse response, @RequestParam(value = "size", required = false, defaultValue = "10") @Valid @Positive long size,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "1") @Valid @Positive long page) throws IOException {
        try {
            List<Article> articles = articleService.getArticles(size, page);
            for (Article a : articles) {
                a.setReplies(replyService.getReplies(a.getAid()));
            }
            return setResponseData(HttpStatus.OK, articles, null);
        } catch (Exception e) {
            e.printStackTrace();
            redirect(response, "localhost:8080/api/articles");
            return null;
        }
    }

    @ApiOperation(value = "게시글 보기", notes = "게시글의 내용을 봅니다.", responseReference = "ResponseDTO<Article>")
    @GetMapping(value = "{aid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<Article>> getArticleById(@PathVariable @Valid @Positive long aid) {
        Article article = articleService.getArticleById(aid);
        if (article == null) {
            return setResponseData(HttpStatus.NOT_FOUND, null, "게시글을 찾을 수 없습니다.");
        } else {
            article.setReplies(replyService.getReplies(aid));
            return setResponseData(HttpStatus.OK, article, null);
        }

    }

    @ApiOperation(value = "게시글 작성", notes = "게시글을 작성합니다.", responseReference = "ResponseDTO<String>")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> addArticle(HttpServletRequest request, @RequestBody @Valid ArticleDTO articleDTO, BindingResult bindingResult) {
        try {
            if (!jwtManager.checkClaim(request.getHeader("jwt"))) {
                return setResponseData(HttpStatus.UNAUTHORIZED, null, "인증되지 않은 요청입니다.");
            }
            if (bindingResult.hasErrors()) {
                return setResponseData(HttpStatus.BAD_REQUEST, parseErrors(bindingResult), "게시글 작성에 실패하였습니다.");
            }
            BigInteger newPost = articleService.addArticle(articleDTO);
            return setResponseData(HttpStatus.CREATED, "http://localhost:8080/api/articles/" + newPost, null);
        } catch (DataAccessException e) {
            return setResponseData(HttpStatus.BAD_REQUEST, null, "게시글 작성에 실패하였습니다.");
        }
    }

    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정합니다.", responseReference = "ResponseDTO<String>")
    @PutMapping(value = "{aid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> updateArticle(HttpServletRequest request, @PathVariable @Valid @Positive long aid, @RequestBody @Valid ArticleDTO articleDTO, BindingResult bindingResult) {
        try {
            if (!jwtManager.checkClaim(request.getHeader("jwt"), articleDTO.getAuthor())) {
                return setResponseData(HttpStatus.UNAUTHORIZED, null, "인증되지 않은 요청입니다.");
            }
            if (bindingResult.hasErrors()) {
                return setResponseData(HttpStatus.BAD_REQUEST, parseErrors(bindingResult), "게시글 수정에 실패하였습니다.");
            }
            boolean isUpdated = articleService.updateArticle(aid, articleDTO) == 1;
            return isUpdated ?
                    setResponseData(HttpStatus.OK, "http://localhost:8080/api/articles/" + aid, null) :
                    setResponseData(HttpStatus.NOT_FOUND, null, "게시글 수정에 실패하였습니다. (해당 게시글을 찾을 수 없습니다.)");
        } catch (DataAccessException e) {
            return setResponseData(HttpStatus.BAD_REQUEST, null, "게시글 수정에 실패하였습니다.");
        }
    }

    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제합니다.", responseReference = "ResponseDTO<String>")
    @DeleteMapping(value = "{aid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> deleteArticle(HttpServletRequest request, @PathVariable @Valid @Positive long aid) {
        try {
            if (!jwtManager.checkClaim(request.getHeader("jwt"))) {
                return setResponseData(HttpStatus.UNAUTHORIZED, null, "인증되지 않은 요청입니다.");
            }
            boolean isDeleted = articleService.deleteArticle(aid) == 1;
            return isDeleted ?
                    setResponseData(HttpStatus.OK, null, "성공적으로 삭제되었습니다.") :
                    setResponseData(HttpStatus.NOT_FOUND, null, "게시글 삭제에 실패하였습니다. (해당 게시글을 찾을 수 없습니다.)");
        } catch (DataAccessException e) {
            return setResponseData(HttpStatus.BAD_REQUEST, null, "게시글 삭제에 실패하였습니다.");
        }
    }
}