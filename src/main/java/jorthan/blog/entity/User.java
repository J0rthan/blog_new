package jorthan.blog.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_name", nullable = false, length = 100, unique = true)
    private String userName;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // setters and getters
    void setUserName(String userName) {
        this.userName = userName;
    }
    String getUserName() {
        return this.userName;
    }

    void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    String getPasswordHash() {
        return this.passwordHash;
    }

    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
}
