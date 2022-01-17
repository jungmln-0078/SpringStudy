package example.mybatis.blog.module;

import example.mybatis.blog.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIHelper {

    public static <T> ResponseEntity<ResponseDTO<T>> setResponseData(HttpStatus httpStatus, @Nullable T data, @Nullable String msg) {
        ResponseDTO<T> responseDTO = ResponseDTO.<T>builder()
                .success(!httpStatus.isError())
                .data(data)
                .msg(msg)
                .build();
        return ResponseEntity.status(httpStatus).body(responseDTO);
    }

    public static <T> ResponseEntity<ResponseDTO<T>> setResponseHeaderJwt(ResponseEntity<ResponseDTO<T>> response, String jwt) {
        return ResponseEntity.status(response.getStatusCode()).header("jwt", jwt).body(response.getBody());

    }

    public static String parseErrors(BindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        List<String> errorMsg = new ArrayList<>();
        for (FieldError e : errors) {
            errorMsg.add(e.getField() + " 는(은) " + e.getDefaultMessage());
        }
        return errorMsg.toString();
    }

    public static void redirect(HttpServletResponse response, String uri) throws IOException {
        response.sendRedirect(uri);
    }
}
