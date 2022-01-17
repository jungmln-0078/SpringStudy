package example.mybatis.blog.module;

import example.mybatis.blog.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static example.mybatis.blog.module.APIHelper.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class InvalidExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ResponseDTO<String>> constraintViolationException(ConstraintViolationException ex) {
        List<String> errorMsg = new ArrayList<>();
        for (ConstraintViolation<?> e : ex.getConstraintViolations()) {
            errorMsg.add(((PathImpl) e.getPropertyPath()).getLeafNode().getName() + " 은(는) " + e.getMessage());
        }
        return setResponseData(HttpStatus.BAD_REQUEST, String.join(" , ", errorMsg), "요청 매개변수가 잘못되었습니다.");
    }
}
