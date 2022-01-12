package example.mybatis.blog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ApiModel(description="게시글")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Article implements Serializable {
    private long aid;

    private String title;

    private long author;

    private String authorString;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdate;

    @Setter
    private List<Reply> replies;
}
