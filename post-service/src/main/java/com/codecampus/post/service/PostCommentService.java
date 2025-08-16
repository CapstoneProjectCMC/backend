package com.codecampus.post.service;

import com.codecampus.post.dto.request.CommentRequestDto;
import com.codecampus.post.dto.response.CommentResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.entity.PostComment;
import com.codecampus.post.repository.PostCommentRepository;
import com.codecampus.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostComment addComment(CommentRequestDto dto, String userId) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUserId(userId);
        comment.setContent(dto.getContent());

        if (dto.getParentCommentId() != null) {
            PostComment parent = postCommentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parent);
        }

        return postCommentRepository.save(comment);
    }

    public List<CommentResponseDto> getCommentsByPost(String postId) {
        List<PostComment> parents = postCommentRepository
                .findByPost_PostIdAndParentCommentIsNullOrderByCommentIdDesc(postId);

        return parents.stream()
                .map(this::mapToDtoWithReplies)
                .toList();
    }

    private CommentResponseDto mapToDtoWithReplies(PostComment comment) {
        List<CommentResponseDto> replyDtos = comment.getReplies() != null
                ? comment.getReplies().stream().map(this::mapToDtoWithReplies).toList()
                : List.of();

        return new CommentResponseDto(
                comment.getCommentId(),
                comment.getUserId(),
                comment.getContent(),
                replyDtos
        );
    }

    @Transactional
    public void updateComment(String commentId, String userId, String newContent) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Not allowed to edit this comment");
        }

        comment.setContent(newContent);
        postCommentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(String commentId, String userId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Not allowed to delete this comment");
        }

        postCommentRepository.delete(comment);
    }
}

