package com.codecampus.post.service;

import com.codecampus.post.config.CustomJwtDecoder;
import com.codecampus.post.dto.request.PostReactionRequestDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.entity.PostReaction;
import com.codecampus.post.repository.PostReactionRepository;
import com.codecampus.post.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReactionService {
    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final CustomJwtDecoder customJwtDecoder;

    public void ReactToPostAndCmt(PostReactionRequestDto requestDto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization token is missing or invalid");
        }

        String userId = customJwtDecoder.decode(token.substring(7))
                .getClaims()
                .get("userId")
                .toString();

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required for post reaction");
        }
        Optional<PostReaction> existingReaction = (requestDto.getCommentId() != null)
                ? postReactionRepository.findByPostIdAndUserIdAndCommentId(
                requestDto.getPostId(), userId, requestDto.getCommentId())
                : postReactionRepository.findByPostIdAndUserId(
                requestDto.getPostId(), userId);

        if (existingReaction.isPresent()) {
            // Reaction đã tồn tại => bỏ reaction (toggle off)
            postReactionRepository.delete(existingReaction.get());
            return;
        }

        // Chưa có reaction => thêm mới (toggle on)
        Post post = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostReaction newReaction = new PostReaction();
        newReaction.setPost(post);
        newReaction.setUserId(userId);
        newReaction.setCommentId(requestDto.getCommentId());
        newReaction.setEmojiType(requestDto.getReactionType());

        postReactionRepository.save(newReaction);
    }

}
