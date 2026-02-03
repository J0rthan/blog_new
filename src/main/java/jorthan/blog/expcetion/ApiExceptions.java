package jorthan.blog.expcetion;

import org.springframework.http.HttpStatus;

/*
status:
    1.  BadRequest（400）：请求参数不合法（空字段、格式不对、长度不对、JSON 解析失败等）
	2.	Unauthorized（401）：未登录/Token 无效/Token 过期/认证失败
	3.	Forbidden（403）：已登录但无权限（例如试图删除别人的资源）
	4.	NotFound（404）：资源不存在（比如“用户不存在”，你也可以选择用 401/404，看你的语义偏好）
	5.	Conflict（409）：资源冲突（注册用户名已存在、重复绑定等）
 */
public class ApiExceptions {
    public static class BadRequest extends ApiException {
        public BadRequest(String code, String message) {
            super(HttpStatus.BAD_REQUEST, code, message);
        }

        public BadRequest(String message) {
            this("REQ_VALIDATION_FAILED", message);
        }
    }

    public static class Unauthorized extends ApiException {
        public Unauthorized(String code, String message) {
            super(HttpStatus.UNAUTHORIZED, code, message);
        }

        public Unauthorized(String message) {
            this("AUTH_UNAUTHORIZED", message);
        }
    }

    public static class Forbidden extends ApiException {
        public Forbidden(String code, String message) {
            super(HttpStatus.FORBIDDEN, code, message);
        }

        public Forbidden(String message) {
            this("AUTH_FORBIDDEN", message);
        }
    }

    public static class NotFound extends ApiException {
        public NotFound(String code, String message) {
            super(HttpStatus.NOT_FOUND, code, message);
        }

        public NotFound(String message) {
            this("RESOURCE_NOT_FOUND", message);
        }
    }

    public static class Conflict extends ApiException {
        public Conflict(String code, String message) {
            super(HttpStatus.CONFLICT, code, message);
        }

        public Conflict(String message) {
            this("RESOURCE_CONFLICT", message);
        }
    }
}
