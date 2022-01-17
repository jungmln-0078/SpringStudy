package example.mybatis.blog.service;

import example.mybatis.blog.mapper.ArticleMapper;
import example.mybatis.blog.model.Article;
import example.mybatis.blog.model.ArticleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Service
@Transactional
public class ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    public List<Article> getArticles(long size, long page) throws Exception {
        if (size <= 0 || page < 0) {
            throw new Exception();
        } else {
            return articleMapper.getArticles(page, size);
        }
    }

    public Article getArticleById(long aid) throws DataAccessException {
        return articleMapper.getArticleById(aid);
    }

    public BigInteger addArticle(ArticleDTO articleDTO) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("author", articleDTO.getAuthor());
        param.put("title", articleDTO.getTitle());
        param.put("content", articleDTO.getContent());
        articleMapper.addArticle(param);
        return (BigInteger) param.get("aid");
    }

    public int updateArticle(long aid, ArticleDTO articleDTO) throws DataAccessException {
        return articleMapper.updateArticle(articleDTO.getTitle(), articleDTO.getContent(), aid);
    }

    public int deleteArticle(long aid) throws DataAccessException {
        return articleMapper.deleteArticle(aid);
    }
}
