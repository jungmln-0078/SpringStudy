package example.mybatis.blog.mapper;

import example.mybatis.blog.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper {
    List<Article> getArticles(@Param("page") long page, @Param("size") long size);

    Article getArticleById(@Param("aid") long aid);

    long addArticle(Map<String, Object> map);

    int updateArticle(@Param("title") String title, @Param("content") String content, @Param("aid") long aid);

    int deleteArticle(@Param("aid") long aid);
}
