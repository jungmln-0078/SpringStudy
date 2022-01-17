package example.mybatis.blog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ApiModel(description="게시글 정보 입력")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO implements Serializable {
    @NotEmpty
    private String title;

    @Positive
    private long author;

    @NotEmpty
    private String content;
}
