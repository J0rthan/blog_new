package jorthan.blog.expcetion;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;

    public ApiException(HttpStatus httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
    public String getCode() {
        return this.code;
    }
}