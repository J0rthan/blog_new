package jorthan.blog.bootstrap;

import jorthan.blog.entity.User;
import jorthan.blog.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    @Value("${app.bootstrap-admin.username:}")
    private String adminUsername;

    @Value("${app.bootstrap-admin.email:}")
    private String adminEmail;

    @Value("${app.bootstrap-admin.password:}")
    private String adminPassword;

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 没配置就不做（避免本地/测试环境误创建）
        if (adminUsername.isBlank() || adminPassword.isBlank() || adminEmail.isBlank()) return;

        // 已经存在管理员就不再创建（只创建一次）
        if (authRepository.existsByIdentity("ADMIN")) return;

        // 避免用户名冲突
        if (authRepository.existsByUserName(adminUsername)) return;

        User admin = new User();
        admin.setUserName(adminUsername);
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setIdentity("ADMIN");
        admin.setExist(true);

        authRepository.save(admin);
    }
}