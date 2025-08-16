package com.codecampus.post.service;

import com.codecampus.post.entity.PostAccess;
import com.codecampus.post.dto.request.PostAccessRequestDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.repository.PostAccessRepository;
import com.codecampus.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostAccessService {

    private final PostAccessRepository postAccessRepository;
    private final PostRepository postRepository;

    // Thêm hoặc cập nhật quyền truy cập cho nhiều user
    @Transactional
    public void saveOrUpdateAccess(PostAccessRequestDto dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<PostAccess> accessList = dto.getUserIds().stream()
                .map(userId -> {
                    PostAccess pa = new PostAccess();
                    pa.setPost(post);
                    pa.setUserId(userId);
                    pa.setIsExcluded(dto.getIsExcluded());
                    return pa;
                })
                .toList();

        postAccessRepository.saveAll(accessList);
    }

    // Xoá quyền của nhiều user
    @Transactional
    public void deleteAccess(String postId, List<String> userIds) {
        postAccessRepository.deleteByPostIdAndUserIdIn(postId, userIds);
    }

    // Lấy danh sách quyền của 1 bài post
    public List<PostAccess> getAccessByPostId(String postId) {
        return postAccessRepository.findByPostId(postId);
    }
}

