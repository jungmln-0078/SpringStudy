package example.mybatis.blog.module;

import example.mybatis.blog.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class APIHelper {

    public static <T> ResponseEntity<ResponseDTO<T>> setResponseData(HttpStatus httpStatus, @Nullable T data, @Nullable String msg) {
        ResponseDTO<T> responseDTO = ResponseDTO.<T>builder()
                .success(!httpStatus.isError())
                .data(data)
                .msg(msg)
                .build();
        return ResponseEntity.status(httpStatus).<ResponseDTO<T>>body(responseDTO);
    }

    public static <T> ResponseEntity<ResponseDTO<T>> setResponseHeaderJwt(ResponseEntity<ResponseDTO<T>> response, String jwt) {
        return ResponseEntity.status(response.getStatusCode()).header("jwt", jwt).body(response.getBody());

    }

    public static void redirect(HttpServletResponse response, String uri) throws IOException {
        response.sendRedirect(uri);
    }
}
