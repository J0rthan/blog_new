package jorthan.blog.service;

import jakarta.servlet.http.HttpServletRequest;
import jorthan.blog.auth.AuthInterceptor;
import jorthan.blog.dtos.CommentDtos;
import jorthan.blog.entity.Comment;
import jorthan.blog.entity.Post;
import jorthan.blog.entity.User;
import jorthan.blog.expcetion.ApiExceptions;
import jorthan.blog.repository.AuthRepository;
import jorthan.blog.repository.CommentRepository;
import jorthan.blog.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


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

    public Page<CommentDtos.CommentListResponse> list(Pageable pageable) {
        return commentRepository.findAllByExist(true, pageable).map(this::toCommentListResponse);
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
}
