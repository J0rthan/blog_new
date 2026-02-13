package jorthan.blog.repository;

import jorthan.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    // 根据用户名查找用户
    Optional<User> findByUserName(String userName);

    // 根据邮箱地址查找用户
    Optional<User> findByEmail(String email);

    // 根据userId和exist查找特定的用户
    @Query("select u from User u where u.id = :id and u.exist = :exist")
    Optional<User> findByIdAndExist(@Param("id") Long id, @Param("exist") Boolean exist);

    boolean existsByIdentity(String identity);
    boolean existsByUserName(String userName);
}
