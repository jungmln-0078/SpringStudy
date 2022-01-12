package example.mybatis.blog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ApiModel(description="게시글 정보 입력")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO implements Serializable {
    private String title;

    private long author;

    private String content;
}
