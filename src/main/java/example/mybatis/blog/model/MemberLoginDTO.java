package example.mybatis.blog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ApiModel(description="로그인용 회원 정보")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginDTO {
    private String email;

    private String password;
}
