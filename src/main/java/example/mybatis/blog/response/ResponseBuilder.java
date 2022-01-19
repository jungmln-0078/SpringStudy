package example.mybatis.blog.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class ResponseBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseEntity.class);

    private HttpStatus httpStatus;
    private ResponseDTO<T> responseDTO;
    private final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    public ResponseBuilder<T> setStatus(HttpStatus status) {
        this.httpStatus = status;
        return this;
    }

    public ResponseBuilder<T> setBody(T data, String msg) {
        this.responseDTO = ResponseDTO.<T>builder()
                .success(!httpStatus.isError())
                .data(data)
                .msg(msg)
                .build();
        return this;
    }

    public ResponseBuilder<T> setHeader(String key, String value) {
        this.headers.add(key, value);
        return this;
    }

    public ResponseEntity<ResponseDTO<T>> build() {
        LOGGER.info(this.httpStatus.toString());
        return new ResponseEntity<>(responseDTO, headers, httpStatus);
    }
}
