package jorthan.blog.repository;

import jorthan.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    // 根据用户名查找用户
    Optional<User> findByUserName(String userName);

    // 根据邮箱地址查找用户
    Optional<User> findByEmail(String email);
}
