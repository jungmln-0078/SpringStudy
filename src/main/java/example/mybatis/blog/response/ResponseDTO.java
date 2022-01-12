package example.mybatis.blog.response;

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
    private boolean success;

    private T data;

    private String msg;
}
