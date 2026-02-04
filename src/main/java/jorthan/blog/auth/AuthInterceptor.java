package jorthan.blog.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jorthan.blog.entity.User;
import jorthan.blog.expcetion.ApiException;
import jorthan.blog.expcetion.ApiExceptions;
import jorthan.blog.repository.AuthRepository;
import jorthan.blog.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    public final static String ATTR_USER_ID = "AUTH_USER_ID";
    private final TokenStore tokenStore;
    private final AuthRepository authRepository;

    public AuthInterceptor(TokenStore tokenStore, AuthRepository authRepository) {
        this.tokenStore = tokenStore;
        this.authRepository = authRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        String path = req.getRequestURI();
        String method = req.getMethod();

        // 放行所有的GET方法和登陆注册请求
        if (method.equals("GET") || path.startsWith("/api/auth")) {
            return true;
        }

        // 通过header获取token
        String token = req.getHeader("Auth-Token");
        if (token == null || token.isBlank()) {
            // 给message
            throw new ApiExceptions.BadRequest("Invalid input token");
        }

        // 通过token查userId
        Long userId = tokenStore.getUserId(token);
        User user = authRepository.findByIdAndExist(userId, Boolean.TRUE).orElseThrow(() -> new ApiExceptions.Unauthorized("Invalid token, cannot get user"));

        // 进行的这一步说明user是存在的，后续都不用判别userId是否查不出来user
        // 将userId存入req中，便于后续业务使用
        req.setAttribute(ATTR_USER_ID, userId);
        return true;
    }
}
