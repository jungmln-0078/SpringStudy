package example.mybatis.blog.controller;

import example.mybatis.blog.aspect.Authentication;
import example.mybatis.blog.model.Article;
import example.mybatis.blog.model.ArticleDTO;
import example.mybatis.blog.model.ArticleWriteDTO;
import example.mybatis.blog.module.JwtManager;
import example.mybatis.blog.response.ResponseBuilder;
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
import javax.validation.constraints.PositiveOrZero;
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
                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") @Valid @PositiveOrZero long page) throws IOException {
        try {
            List<Article> articles = articleService.getArticles(size, page);
            for (Article a : articles) {
                a.setReplies(replyService.getReplies(a.getAid()));
            }
            return new ResponseBuilder<List<Article>>()
                    .setStatus(HttpStatus.OK)
                    .setBody(articles, String.valueOf(articles.size()))
                    .build();
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
            return new ResponseBuilder<Article>()
                    .setStatus(HttpStatus.NOT_FOUND)
                    .setBody(null, "게시글을 찾을 수 없습니다.")
                    .build();
        } else {
            article.setReplies(replyService.getReplies(aid));
            return new ResponseBuilder<Article>()
                    .setStatus(HttpStatus.OK)
                    .setBody(article, null)
                    .build();
        }

    }

    @Authentication
    @ApiOperation(value = "게시글 작성", notes = "게시글을 작성합니다.", responseReference = "ResponseDTO<String>")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> addArticle(HttpServletRequest request, @RequestBody @Valid ArticleWriteDTO articleWriteDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseBuilder<String>()
                        .setStatus(HttpStatus.BAD_REQUEST)
                        .setBody(parseErrors(bindingResult), "게시글 작성에 실패하였습니다.")
                        .build();
            }
            ArticleDTO articleDTO = new ArticleDTO(articleWriteDTO.getTitle(), jwtManager.getJwtMid(request.getHeader("jwt")), articleWriteDTO.getContent());
            BigInteger newPost = articleService.addArticle(articleDTO);
            return new ResponseBuilder<String>()
                    .setStatus(HttpStatus.CREATED)
                    .setHeader("Location", "http://localhost:8080/api/articles/" + newPost)
                    .setBody(null, "성공적으로 생성되었습니다.")
                    .build();
        } catch (DataAccessException e) {
            return new ResponseBuilder<String>()
                    .setStatus(HttpStatus.BAD_REQUEST)
                    .setBody(null, "게시글 작성에 실패하였습니다.")
                    .build();
        }
    }

    @Authentication
    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정합니다.", responseReference = "ResponseDTO<String>")
    @PutMapping(value = "{aid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> updateArticle(HttpServletRequest request, @PathVariable @Valid @Positive long aid, @RequestBody @Valid ArticleWriteDTO articleWriteDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseBuilder<String>()
                        .setStatus(HttpStatus.BAD_REQUEST)
                        .setBody(parseErrors(bindingResult), "게시글 수정에 실패하였습니다.")
                        .build();
            }
            boolean isUpdated = articleService.updateArticle(aid, articleWriteDTO) == 1;
            return isUpdated ?
                    new ResponseBuilder<String>()
                            .setStatus(HttpStatus.OK)
                            .setHeader("Location", "http://localhost:8080/api/articles/" + aid)
                            .setBody(null, "성공적으로 수정되었습니다.")
                            .build() :
                    new ResponseBuilder<String>()
                            .setStatus(HttpStatus.NOT_FOUND)
                            .setBody(null, "게시글 수정에 실패하였습니다. (해당 게시글을 찾을 수 없습니다.")
                            .build();
        } catch (DataAccessException e) {
            return new ResponseBuilder<String>()
                    .setStatus(HttpStatus.BAD_REQUEST)
                    .setBody(null, "게시글 수정에 실패하였습니다.")
                    .build();
        }
    }

    @Authentication
    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제합니다.", responseReference = "ResponseDTO<String>")
    @DeleteMapping(value = "{aid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<String>> deleteArticle(HttpServletRequest request, @PathVariable @Valid @Positive long aid) {
        try {
            boolean isDeleted = articleService.deleteArticle(aid) == 1;
            return isDeleted ?
                    new ResponseBuilder<String>()
                            .setStatus(HttpStatus.OK)
                            .setBody(null, "성공적으로 삭제되었습니다.")
                            .build() :
                    new ResponseBuilder<String>()
                            .setStatus(HttpStatus.NOT_FOUND)
                            .setBody(null, "게시글 삭제에 실패하였습니다. (해당 게시글을 찾을 수 없습니다.)")
                            .build();
        } catch (DataAccessException e) {
            return new ResponseBuilder<String>()
                    .setStatus(HttpStatus.BAD_REQUEST)
                    .setBody(null, "게시글 삭제에 실패하였습니다.")
                    .build();
        }
    }
}