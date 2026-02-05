package jorthan.blog.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "exist", nullable = false)
    private boolean exist;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "delted_at", nullable = true)
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.deletedAt = LocalDateTime.now();
    }

    // getters and setters
    public Long getId() {return this.id;}
    public void setId(Long id) {this.id = id;}

    public User getUser() {return this.user;}
    public void setUser(User user) {this.user = user;}

    public Post getPost() {return this.post;}
    public void setPost(Post post) {this.post = post;}

    public String getContent() {return this.content;}
    public void setContent(String content) {this.content = content;}

    public boolean getExist() {return this.exist;}
    public void setExist(boolean exist) {this.exist = exist;}

    public LocalDateTime getCreatedAt() {return this.createdAt;}
    public void setCreatedAt(LocalDateTime time) {this.createdAt = time;}

    public LocalDateTime getDeletedAt() {return this.deletedAt;}
    public void setDeletedAt(LocalDateTime time) {this.deletedAt = time;}
}
