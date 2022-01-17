package example.mybatis.blog.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDTO<T> implements Serializable {
    @ApiModelProperty(value = "요청 처리 결과 True/False")
    private boolean success;

    @ApiModelProperty(value = "결과 데이터")
    private T data;

    @ApiModelProperty(value = "결과 메세지")
    private String msg;
}
