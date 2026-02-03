package jorthan.blog.service;

import jorthan.blog.dtos.PostDtos;
import jorthan.blog.entity.Post;
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
        Page<PostDtos.PostSummaryResponse> lists = postRepository.findAll(pageable).map(this::toPostSummaryResponse);

        return lists;
    }

    public PostDtos.PostDetailResponse submit(Pageable pageable, Long userId, PostDtos.PostSubmitRequest body) {
        Post post = new Post();
        post.setTitle(body.title());
        post.setContent(body.content());
        post.setSummary(body.summary());
        post.setAuthor(authRepository.findById(userId).get());
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
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }
}
