package jorthan.blog.repository;

import jorthan.blog.entity.Comment;
import jorthan.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndExist(Long id, boolean exist);

    @Query("select c from Comment c where c.post.id = :postId and c.exist = :exist")
    Page<Comment> findByPostIdAndExist(Long postId, boolean exist, Pageable pageable);
}
