package example.mybatis.blog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ApiModel(description = "회원 정보 입력")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO implements Serializable {
    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
