package example.mybatis.blog.module;

import example.mybatis.blog.response.ResponseBuilder;
import example.mybatis.blog.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIHelper {

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

    public static ResponseEntity<ResponseDTO<String>> responseBindingError(BindingResult bindingResult, String msg) {
        return new ResponseBuilder<String>()
                .setStatus(HttpStatus.BAD_REQUEST)
                .setBody(parseErrors(bindingResult), msg)
                .build();
    }
}
