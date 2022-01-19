package example.mybatis.blog.aspect;

import example.mybatis.blog.module.UnAuthorizedException;
import example.mybatis.blog.response.ResponseBuilder;
import example.mybatis.blog.response.ResponseDTO;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ResponseDTO<String>> constraintViolationException(ConstraintViolationException ex) {
        List<String> errorMsg = new ArrayList<>();
        for (ConstraintViolation<?> e : ex.getConstraintViolations()) {
            errorMsg.add(((PathImpl) e.getPropertyPath()).getLeafNode().getName() + " 은(는) " + e.getMessage());
        }
        return new ResponseBuilder<String>()
                .setStatus(HttpStatus.BAD_REQUEST)
                .setBody(String.join(" , ", errorMsg), "요청 매개변수가 잘못되었습니다.")
                .build();
    }

    @ExceptionHandler(UnAuthorizedException.class)
    protected ResponseEntity<ResponseDTO<String>> unAuthorizedException(UnAuthorizedException ex) {
        return new ResponseBuilder<String>()
                .setStatus(HttpStatus.UNAUTHORIZED)
                .setBody(null, "인증되지 않은 요청입니다.")
                .build();
    }
}
