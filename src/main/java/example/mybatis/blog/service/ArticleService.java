package example.mybatis.blog.service;

import example.mybatis.blog.mapper.ArticleMapper;
import example.mybatis.blog.model.Article;
import example.mybatis.blog.model.ArticleDTO;
import example.mybatis.blog.model.ArticleWriteDTO;
import example.mybatis.blog.response.ResponseBuilder;
import example.mybatis.blog.response.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ReplyService replyService;

    public ResponseEntity<ResponseDTO<List<Article>>> getArticles(long size, long page) throws DataAccessException {
        List<Article> articles = articleMapper.getArticles(page * size, size);
        for (Article a : articles) {
            a.setReplies(replyService.getReplies(a.getAid()));
        }
        return new ResponseBuilder<List<Article>>()
                .setStatus(HttpStatus.OK)
                .setBody(articles, String.valueOf(articles.size()))
                .build();
    }

    public ResponseEntity<ResponseDTO<Article>> getArticleById(long aid) throws DataAccessException {
        Article article = articleMapper.getArticleById(aid);
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

    public Article getArticleByIdRaw(long aid) throws DataAccessException {
        return articleMapper.getArticleById(aid);
    }

    public ResponseEntity<ResponseDTO<String>> addArticle(ArticleDTO articleDTO) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("author", articleDTO.getAuthor());
        param.put("title", articleDTO.getTitle());
        param.put("content", articleDTO.getContent());
        articleMapper.addArticle(param);
        return new ResponseBuilder<String>()
                .setStatus(HttpStatus.CREATED)
                .setHeader("Location", "http://localhost:8081/api/articles/" + param.get("aid"))
                .setBody(null, "성공적으로 생성되었습니다.")
                .build();
    }

    public ResponseEntity<ResponseDTO<String>> updateArticle(long aid, ArticleWriteDTO articleWriteDTO) throws DataAccessException {
        boolean isUpdated = articleMapper.updateArticle(articleWriteDTO.getTitle(), articleWriteDTO.getContent(), aid) == 1;
        return isUpdated ?
                new ResponseBuilder<String>()
                        .setStatus(HttpStatus.OK)
                        .setHeader("Location", "http://localhost:8081/api/articles/" + aid)
                        .setBody(null, "성공적으로 수정되었습니다.")
                        .build() :
                new ResponseBuilder<String>()
                        .setStatus(HttpStatus.NOT_FOUND)
                        .setBody(null, "게시글 수정에 실패하였습니다. (해당 게시글을 찾을 수 없습니다.")
                        .build();
    }

    public ResponseEntity<ResponseDTO<String>> deleteArticle(long aid) throws DataAccessException {
        boolean isDeleted = articleMapper.deleteArticle(aid) == 1;
        return isDeleted ?
                new ResponseBuilder<String>()
                        .setStatus(HttpStatus.OK)
                        .setBody(null, "성공적으로 삭제되었습니다.")
                        .build() :
                new ResponseBuilder<String>()
                        .setStatus(HttpStatus.NOT_FOUND)
                        .setBody(null, "게시글 삭제에 실패하였습니다. (해당 게시글을 찾을 수 없습니다.)")
                        .build();
    }
}
