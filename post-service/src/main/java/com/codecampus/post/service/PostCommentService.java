package com.codecampus.post.service;

import com.codecampus.post.config.CustomJwtDecoder;
import com.codecampus.post.dto.request.CommentRequestDto;
import com.codecampus.post.dto.request.UpdateCommentDto;
import com.codecampus.post.dto.response.CommentResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.entity.PostComment;
import com.codecampus.post.repository.PostCommentRepository;
import com.codecampus.post.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final CustomJwtDecoder customJwtDecoder;

    @Transactional
    public PostComment addComment(CommentRequestDto dto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization token is missing or invalid");
        }
        String userId = customJwtDecoder.decode(token.substring(7)).getClaims().get("userId").toString(); // Assuming user ID is stored in the principal

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
                .filter(c -> !c.isDeleted()) // chỉ lấy comment chưa xóa
                .map(c -> mapWithLimitDepth(c, 1)) // bắt đầu từ depth = 1
                .toList();
    }

    private CommentResponseDto mapWithLimitDepth(PostComment comment, int depth) {
        List<CommentResponseDto> replyDtos;

        if (depth < 2) {
            // cấp 1 -> cho phép lấy reply
            replyDtos = comment.getReplies() != null
                    ? comment.getReplies().stream()
                    .filter(r -> !r.isDeleted())
                    .flatMap(r -> {
                        if (depth + 1 < 2) {
                            // vẫn còn trong giới hạn, tiếp tục đệ quy
                            return Stream.of(mapWithLimitDepth(r, depth + 1));
                        } else {
                            // cấp 2 rồi, gom hết con/cháu thành cấp 2 luôn
                            return flattenReplies(r).stream();
                        }
                    })
                    .toList()
                    : List.of();
        } else {
            // depth >= 2 thì không bao giờ xảy ra vì ta dừng ở depth=1 rồi flatten
            replyDtos = List.of();
        }

        return new CommentResponseDto(
                comment.getCommentId(),
                comment.getUserId(),
                comment.getContent(),
                replyDtos
        );
    }

    /**
     * Flatten toàn bộ reply từ comment con (cấp >= 2) thành danh sách cấp 2
     */
    private List<CommentResponseDto> flattenReplies(PostComment comment) {
        List<CommentResponseDto> flatList = new ArrayList<>();

        if (!comment.isDeleted()) {
            flatList.add(new CommentResponseDto(
                    comment.getCommentId(),
                    comment.getUserId(),
                    comment.getContent(),
                    List.of() // ép thành cấp 2 -> không còn replies
            ));
        }

        if (comment.getReplies() != null) {
            for (PostComment child : comment.getReplies()) {
                flatList.addAll(flattenReplies(child)); // đệ quy gom vào 1 list
            }
        }

        return flatList;
    }






    @Transactional
    public void updateComment(UpdateCommentDto requestDto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization token is missing or invalid");
        }
        String userId = customJwtDecoder.decode(token.substring(7)).getClaims().get("userId").toString(); // Assuming user ID is stored in the principal
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required for updating comment");
        }

        PostComment comment = postCommentRepository.findById(requestDto.getCommentId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Not allowed to edit this comment");
        }

        comment.setContent(requestDto.getContent());
        postCommentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(String commentId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization token is missing or invalid");
        }
        String userId = customJwtDecoder.decode(token.substring(7)).getClaims().get("userId").toString();
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required for deleting comment");
        }

        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.isDeleted()) {
            comment.markDeleted(userId);
            postCommentRepository.save(comment);
        }
    }
}

