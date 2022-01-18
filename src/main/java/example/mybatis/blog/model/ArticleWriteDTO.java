package example.mybatis.blog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ApiModel(description = "게시글 정보 입력")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleWriteDTO implements Serializable {
    @NotEmpty
    private String title;

    @NotEmpty
    private String content;
}
