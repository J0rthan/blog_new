package jorthan.blog.repository;

import jorthan.blog.entity.Comment;
import jorthan.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByExist(boolean exist, Pageable pageable);
}
