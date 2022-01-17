package example.mybatis.blog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ApiModel(description = "로그인용 회원 정보")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginDTO {
    @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
