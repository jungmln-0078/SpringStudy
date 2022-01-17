package example.mybatis.blog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ApiModel(description="댓글")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO implements Serializable {
    @Positive
    private long aid;

    @Positive
    private long author;

    @NotEmpty
    private String content;
}