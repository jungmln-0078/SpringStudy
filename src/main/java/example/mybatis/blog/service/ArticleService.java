package example.mybatis.blog.service;

import example.mybatis.blog.model.Article;
import example.mybatis.blog.model.ArticleDTO;
import org.mybatis.spring.SqlSessionTemplate;
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
    private SqlSessionTemplate sqlSessionTemplate;

    public List<Article> getArticles(long size, long page) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("size", size);
        param.put("page", -size + page * size);
        if ((long) param.get("size") <= 0 || (long) param.get("page") < 0) {
            throw new Exception();
        } else {
            return sqlSessionTemplate.selectList("mybatis.ArticleMapper.getArticles", param);
        }
    }

    public Article getArticleById(long aid) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("aid", aid);
        return sqlSessionTemplate.selectOne("mybatis.ArticleMapper.getArticleById", param);
    }

    public BigInteger addArticle(ArticleDTO articleDTO) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("author", articleDTO.getAuthor());
        param.put("title", articleDTO.getTitle());
        param.put("content", articleDTO.getContent());
        sqlSessionTemplate.insert("mybatis.ArticleMapper.addArticle", param);
        return (BigInteger) param.get("aid");
    }

    public int updateArticle(long aid, ArticleDTO articleDTO) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("aid", aid);
        param.put("title", articleDTO.getTitle());
        param.put("content", articleDTO.getContent());
        return sqlSessionTemplate.update("mybatis.ArticleMapper.updateArticle", param);
    }

    public int deleteArticle(long aid) throws DataAccessException {
        Map<String, Object> param = new HashMap<>();
        param.put("aid", aid);
        return sqlSessionTemplate.delete("mybatis.ArticleMapper.deleteArticle", param);
    }
}
