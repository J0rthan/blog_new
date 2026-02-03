package jorthan.blog.expcetion;

import jakarta.servlet.http.HttpServletRequest;
import jorthan.blog.dtos.AuthDtos;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // String code,
    // String message,
    // int status,
    // String path,
    // Instant timestamp
    // 处理自定义exception
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<AuthDtos.ErrorResponse> handleApi(ApiException e, HttpServletRequest req) {
        var errorResponse = new AuthDtos.ErrorResponse(
                e.getCode(),
                e.getMessage(),
                e.getHttpStatus().value(),
                req.getRequestURI(),
                Instant.now()
        );

        return ResponseEntity.status(e.getHttpStatus()).body(errorResponse);
    }

    // 处理@Valid参数校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthDtos.ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .orElse("Validation failed");

        var body = new AuthDtos.ErrorResponse(
                "REQ_VALIDATION_FAILED",
                msg,
                400,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.badRequest().body(body);
    }

    // JSON 格式错误 / 字段类型不匹配
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AuthDtos.ErrorResponse> handleBadJson(HttpMessageNotReadableException e, HttpServletRequest req) {
        var body = new AuthDtos.ErrorResponse(
                "REQ_MALFORMED_JSON",
                "Malformed JSON request",
                400,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.badRequest().body(body);
    }

    // 其他Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthDtos.ErrorResponse> handleException(Exception e, HttpServletRequest req) {
        var body = new AuthDtos.ErrorResponse(
                "INTERNAL_ERROR",
                "Internal server error",
                500,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(500).body(body);
    }
}
