package example.mybatis.blog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ApiModel(description="댓글")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO implements Serializable {
    private long aid;

    private long author;

    private String content;
}