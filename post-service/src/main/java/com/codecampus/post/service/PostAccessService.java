package com.codecampus.post.service;

import com.codecampus.post.dto.request.PostAccessRequestDto;
import com.codecampus.post.dto.response.PostAccessResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.entity.PostAccess;
import com.codecampus.post.repository.PostAccessRepository;
import com.codecampus.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // lấy danh sách userId đã có
    Set<String> existingUserIds =
        postAccessRepository.findByPost_PostId(dto.getPostId())
            .stream()
            .map(PostAccess::getUserId)
            .collect(Collectors.toSet());

    // chỉ giữ lại userId chưa có
    List<PostAccess> newAccessList = dto.getUserIds().stream()
        .filter(userId -> !existingUserIds.contains(userId))
        .map(userId -> {
          PostAccess pa = new PostAccess();
          pa.setPost(post);
          pa.setUserId(userId);
          pa.setIsExcluded(dto.getIsExcluded());
          return pa;
        })
        .toList();

    if (!newAccessList.isEmpty()) {
      postAccessRepository.saveAll(newAccessList);
    }
  }


  // Xoá quyền của nhiều user
  @Transactional
  public void deleteAccess(PostAccessRequestDto dto) {
    postAccessRepository.deleteByPost_PostIdAndUserIdIn(dto.getPostId(),
        dto.getUserIds());
  }

  // Lấy danh sách quyền của 1 bài post
  @Transactional
  public List<PostAccessResponseDto> getAccessByPostId(String postId) {
    return postAccessRepository.findByPost_PostId(postId).stream()
        .map(pa -> PostAccessResponseDto.builder()
            .postId(pa.getPost().getPostId())
            .userId(pa.getUserId())
            .isExcluded(Boolean.TRUE.equals(pa.getIsExcluded()))
            .build()
        )
        .toList();
  }

}

