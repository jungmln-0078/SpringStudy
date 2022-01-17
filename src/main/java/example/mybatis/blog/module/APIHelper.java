package example.mybatis.blog.module;

import example.mybatis.blog.controller.ArticleController;
import example.mybatis.blog.response.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    public static <T> ResponseEntity<ResponseDTO<T>> setResponseData(HttpStatus httpStatus, @Nullable T data, @Nullable String msg) {
        ResponseDTO<T> responseDTO = ResponseDTO.<T>builder()
                .success(!httpStatus.isError())
                .data(data)
                .msg(msg)
                .build();
        ResponseEntity<ResponseDTO<T>> responseDTOResponseEntity = ResponseEntity.status(httpStatus).body(responseDTO);
        LOGGER.info(responseDTOResponseEntity.toString());
        return responseDTOResponseEntity;
    }

    public static <T> ResponseEntity<ResponseDTO<T>> setResponseHeaderJwt(ResponseEntity<ResponseDTO<T>> response, String jwt) {
        return ResponseEntity.status(response.getStatusCode()).header("jwt", jwt).body(response.getBody());

    }

    public static String parseErrors(BindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        List<String> errorMsg = new ArrayList<>();
        for (FieldError e : errors) {
            errorMsg.add(e.getField() + " 은(는) " + e.getDefaultMessage());
        }
        return String.join(" , ", errorMsg);
    }

    public static void redirect(HttpServletResponse response, String uri) throws IOException {
        response.sendRedirect(uri);
    }
}
