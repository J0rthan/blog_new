package jorthan.blog.service;

import jakarta.servlet.http.HttpServletRequest;
import jorthan.blog.auth.AuthInterceptor;
import jorthan.blog.dtos.PostDtos;
import jorthan.blog.entity.Post;
import jorthan.blog.expcetion.ApiException;
import jorthan.blog.expcetion.ApiExceptions;
import jorthan.blog.repository.AuthRepository;
import jorthan.blog.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final AuthRepository authRepository;

    public PostService(PostRepository postRepository, AuthRepository authRepository) {
        this.postRepository = postRepository;
        this.authRepository = authRepository;
    }

    public Page<PostDtos.PostSummaryResponse> list(Pageable pageable) {
        Page<PostDtos.PostSummaryResponse> lists = postRepository.findAllByExist(true, pageable).map(this::toPostSummaryResponse);

        return lists;
    }

    public PostDtos.PostDetailResponse submit(Long userId, PostDtos.PostRequest body) {
        Post post = new Post();
        post.setTitle(body.title());
        post.setContent(body.content());
        post.setSummary(body.summary());
        post.setExist(true);
        post.setCategory(body.category());
        post.setAuthor(authRepository.findById(userId).get());
        post = postRepository.save(post);

        return toPostDetailResponse(post);
    }

    public PostDtos.PostDetailResponse read(Long postId) {
        Post post = postRepository.findByIdAndExist(postId, true).orElseThrow(() -> new ApiExceptions.NotFound("post not found"));

        return toPostDetailResponse(post);
    }

    public PostDtos.PostDetailResponse update(PostDtos.PostRequest body, HttpServletRequest req, Long postId) {
        // 先判断是否为作者，不是作者不能修改
        Long userId = (Long)req.getAttribute(AuthInterceptor.ATTR_USER_ID);
        // 查不到post说明post不存在
        Post post = postRepository.findByIdAndExist(postId, true).orElseThrow(() -> new ApiExceptions.NotFound("post not found"));
        Long authorId = post.getAuthor().getId();
        if (!userId.equals(authorId)) {
            throw new ApiExceptions.Forbidden("Only author can modify this post");
        }

        // 进行修改操作
        post.setTitle(body.title());
        post.setContent(body.content());
        post.setSummary(body.summary());
        post.setCategory(body.category());
        post = postRepository.save(post);

        return toPostDetailResponse(post);
    }

    public PostDtos.PostDeleteResponse delete(HttpServletRequest req, Long postId) {
        // 先判断是否为作者，不是作者不能修改
        Long userId = (Long)req.getAttribute(AuthInterceptor.ATTR_USER_ID);
        // 查不到post说明post不存在
        Post post = postRepository.findByIdAndExist(postId, true).orElseThrow(() -> new ApiExceptions.NotFound("post not found"));
        Long authorId = post.getAuthor().getId();
        if (!userId.equals(authorId)) {
            throw new ApiExceptions.Forbidden("Only author can modify this post");
        }

        // 进行删除操作
        post.setExist(false);
        post = postRepository.save(post);

        return toPostDeleteResponse(post);
    }

    public PostDtos.PostDetailResponse restore(HttpServletRequest req, Long postId) {
        // 先判断是否为作者，不是作者不能修改
        Long userId = (Long)req.getAttribute(AuthInterceptor.ATTR_USER_ID);
        // 查不到post说明post不存在
        Post post = postRepository.findByIdAndExist(postId, true).orElseThrow(() -> new ApiExceptions.NotFound("post not found"));
        Long authorId = post.getAuthor().getId();
        if (!userId.equals(authorId)) {
            throw new ApiExceptions.Forbidden("Only author can modify this post");
        }

        // 进行恢复操作
        post.setExist(true);
        post = postRepository.save(post);

        return toPostDetailResponse(post);
    }

    // 将Post转换成PostDtos.PostSummaryResponse
    //    Long id,
    //    String authorName,
    //    String title,
    //    String summary,
    //    LocalDateTime createdAt,
    //    LocalDateTime modifiedAt
    public PostDtos.PostSummaryResponse toPostSummaryResponse(Post post) {
        return new PostDtos.PostSummaryResponse(
                post.getId(),
                post.getAuthor().getUserName(),
                post.getTitle(),
                post.getSummary(),
                post.getExist(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }

    // 将Post转换成PostDtos.PostDetailResponse
    //    Long id,
    //    String authorName,
    //    String title,
    //    String content,
    //    LocalDateTime createdAt,
    //    LocalDateTime modifiedAt
    public PostDtos.PostDetailResponse toPostDetailResponse(Post post) {
        return new PostDtos.PostDetailResponse(
                post.getId(),
                post.getAuthor().getUserName(),
                post.getTitle(),
                post.getContent(),
                post.getExist(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }

    //  将Post转换成PostDtos.PostDeleteResponse
    //    Long id,
    //    String title,
    //    String summary,
    //    String message
    public PostDtos.PostDeleteResponse toPostDeleteResponse(Post post) {
        return new PostDtos.PostDeleteResponse(
                post.getId(),
                post.getTitle(),
                post.getSummary(),
                post.getExist(),
                "Successfully delete"
        );
    }
}
