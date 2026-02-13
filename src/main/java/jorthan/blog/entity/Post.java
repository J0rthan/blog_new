package jorthan.blog.entity;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

@Entity
@Table(name="post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = true)
    private LocalDateTime modifiedAt;

    @Column(name = "exist", nullable = false)
    private boolean exist;

    @Column(name = "category", nullable = false)
    private String category;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            LocalDateTime time = LocalDateTime.now();
            this.createdAt = time;
        }
    }

    @PreUpdate
    public void preUpdate() {
        LocalDateTime time = LocalDateTime.now();
        this.modifiedAt = time;
    }

    // getters and setters
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public User getAuthor() {
        return this.author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return this.summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(LocalDateTime time) {
        this.createdAt = time;
    }

    public LocalDateTime getModifiedAt() {
        return this.modifiedAt;
    }
    public void setModifiedAt(LocalDateTime time) {
        this.modifiedAt = time;
    }

    public boolean getExist() { return this.exist; }
    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public String getCategory() { return this.category; }
    public void setCategory(String category) { this.category = category; }
}
