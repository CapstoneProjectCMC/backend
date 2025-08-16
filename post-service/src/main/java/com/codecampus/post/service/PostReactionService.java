package com.codecampus.post.service;

import com.codecampus.post.config.CustomJwtDecoder;
import com.codecampus.post.dto.request.PostReactionRequestDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.entity.PostReaction;
import com.codecampus.post.repository.PostReactionRepository;
import com.codecampus.post.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReactionService {
    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final CustomJwtDecoder customJwtDecoder;

    @Transactional
    public void toggleReaction(PostReactionRequestDto requestDto, HttpServletRequest request) {
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

        // Kiểm tra reaction đã tồn tại chưa
        Optional<PostReaction> existingReaction = (requestDto.getCommentId() != null)
                ? postReactionRepository.findByPost_PostIdAndUserIdAndCommentId(
                requestDto.getPostId(), userId, requestDto.getCommentId())
                : postReactionRepository.findByPost_PostIdAndUserId(
                requestDto.getPostId(), userId);

        if (existingReaction.isPresent()) {
            PostReaction reaction = existingReaction.get();

            if (reaction.getEmojiType().equals(requestDto.getReactionType())) {
                // Nếu user bấm lại cùng loại reaction -> xóa
                postReactionRepository.delete(reaction);
                return;
            } else {
                // Nếu user chọn reaction khác -> update
                reaction.setEmojiType(requestDto.getReactionType());
                postReactionRepository.save(reaction);
                return;
            }
        }

        //chưa có reaction nào -> tạo mới
        Post post = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostReaction newReaction = new PostReaction();
        newReaction.setPost(post);
        newReaction.setUserId(userId);
        newReaction.setCommentId(requestDto.getCommentId());
        newReaction.setEmojiType(requestDto.getReactionType());

        postReactionRepository.save(newReaction);
    }

    @Transactional
    public Map<String, Long> getReactionCount(String postId, String commentId) {
        long upvotes;
        long downvotes;

        if (commentId == null) {
            // Reaction cho post
            upvotes = postReactionRepository.countByPost_PostIdAndEmojiType(postId, "upvote");
            downvotes = postReactionRepository.countByPost_PostIdAndEmojiType(postId, "downvote");
        } else {
            // Reaction cho comment trong post
            upvotes = postReactionRepository.countByPost_PostIdAndCommentIdAndEmojiType(postId, commentId, "upvote");
            downvotes = postReactionRepository.countByPost_PostIdAndCommentIdAndEmojiType(postId, commentId, "downvote");
        }

        Map<String, Long> result = new HashMap<>();
        result.put("upvote", upvotes);
        result.put("downvote", downvotes);
        return result;
    }

}
