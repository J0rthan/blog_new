package jorthan.blog.service;

import jakarta.servlet.http.HttpServletRequest;
import jorthan.blog.auth.AuthInterceptor;
import jorthan.blog.dtos.CommentDtos;
import jorthan.blog.entity.Comment;
import jorthan.blog.entity.Post;
import jorthan.blog.entity.User;
import jorthan.blog.expcetion.ApiException;
import jorthan.blog.expcetion.ApiExceptions;
import jorthan.blog.repository.AuthRepository;
import jorthan.blog.repository.CommentRepository;
import jorthan.blog.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final AuthRepository authRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, AuthRepository authRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.authRepository = authRepository;
        this.postRepository = postRepository;
    }

    public Page<CommentDtos.CommentListResponse> list(Pageable pageable, Long postId) {
        return commentRepository.findByPostIdAndExist(postId, true, pageable).map(this::toCommentListResponse);
    }

    public CommentDtos.CommentListResponse submit(CommentDtos.CommentRequest body, HttpServletRequest req, Long postId) {
        // 先获取userId与User
        Long userId = (Long) req.getAttribute(AuthInterceptor.ATTR_USER_ID);
        User user = authRepository.findById(userId).get();

        // 再查post
        Post post = postRepository.findByIdAndExist(postId, true).orElseThrow(() -> new ApiExceptions.NotFound("post not found"));

        // 创建Comment对象
        Comment comment = new Comment();
        comment.setContent(body.content());
        comment.setExist(true);
        comment.setUser(user);
        comment.setPost(post);
        comment = commentRepository.save(comment);

        return toCommentListResponse(comment);
    }

    public CommentDtos.CommentDeleteResponse delete(HttpServletRequest req, Long postId, Long commentId) {
        // 先获取userId与User
        Long userId = (Long) req.getAttribute(AuthInterceptor.ATTR_USER_ID);
        User user = authRepository.findById(userId).get();

        // 再查post
        Post post = postRepository.findByIdAndExist(postId, true).orElseThrow(() -> new ApiExceptions.NotFound("post not found"));

        // 再查comment
        Comment comment = commentRepository.findByIdAndExist(commentId, true).orElseThrow(() -> new ApiExceptions.NotFound("comment not found"));

        // 只有comment的发送者或者文章作者才能删除评论
        Long authorId  = post.getAuthor().getId();
        Long reviewerId = comment.getUser().getId();
        if (!userId.equals(authorId) && !userId.equals(reviewerId)) {
            throw new ApiExceptions.Forbidden("Only post-author and sender of this comment can delete comment");
        }

        // 删除comment
        comment.setExist(false);
        comment = commentRepository.save(comment);

        return toCommentDeleteResponse(comment);
    }

    // 将Comment对象转化为CommentDtos.CommentListResponse
    //    String userName,
    //    String content,
    //    LocalDateTime createdAt
    public CommentDtos.CommentListResponse toCommentListResponse(Comment comment) {
        return new CommentDtos.CommentListResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getUserName(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    // 将Comment对象转化为CommentDtos.CommentDeleteResponse
    //    Long commentId,
    //    Long postId,
    //    String userName,
    //    String content,
    //    LocalDateTime deletedAt
    public CommentDtos.CommentDeleteResponse toCommentDeleteResponse(Comment comment) {
        return new CommentDtos.CommentDeleteResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getUserName(),
                comment.getContent(),
                comment.getDeletedAt()
        );
    }
}
