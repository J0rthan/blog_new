package jorthan.blog.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, length = 100, unique = true)
    private String userName;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "identity", nullable = false)
    private String identity;

    @Column(name = "exist", nullable = false)
    private Boolean exist;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // setters and getters
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return this.id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserName() {
        return this.userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return this.email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setExist(Boolean exist) {
        this.exist = exist;
    }
    public Boolean getExist() {
        return this.exist;
    }

    public void setDeletedAt(LocalDateTime time) {
        this.deletedAt = time;
    }
    public LocalDateTime getDeletedAt() {
        return this.deletedAt;
    }

    public void setIdentity(String identity) { this.identity = identity; }
    public String getIdentity() { return this.identity; }
}
