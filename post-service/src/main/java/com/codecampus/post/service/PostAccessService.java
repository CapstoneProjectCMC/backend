package com.codecampus.post.service;

import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.request.PostAccessUpsertRequestDto;
import com.codecampus.post.dto.response.PostAccessResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.entity.PostAccess;
import com.codecampus.post.exception.AppException;
import com.codecampus.post.exception.ErrorCode;
import com.codecampus.post.helper.AuthenticationHelper;
import com.codecampus.post.repository.PostAccessRepository;
import com.codecampus.post.repository.PostRepository;
import com.codecampus.post.service.kafka.PostAccessEventProducer;
import com.codecampus.post.utils.PageResponseUtils;
import events.post.PostAccessEvent;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostAccessService {

  PostAccessRepository postAccessRepository;
  PostRepository postRepository;
  PostAccessEventProducer postAccessEventProducer;

  // Thêm hoặc cập nhật quyền truy cập cho nhiều user
  @Transactional
  public void upsertAccess(
      String postId,
      PostAccessUpsertRequestDto dto) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

    // Tất cả access hiện có
    List<PostAccess> exists = postAccessRepository.findByPost_PostId(postId);

    // --- Cập nhật / thêm ---
    Map<String, PostAccess> map = exists.stream()
        .collect(Collectors.toMap(PostAccess::getUserId, a -> a));

    for (String uid : dto.getUserIds()) {
      PostAccess pa = map.get(uid);
      if (pa == null) {
        pa = PostAccess.builder()
            .post(post)
            .userId(uid)
            .isExcluded(dto.getIsExcluded())
            .build();
      } else {
        pa.setIsExcluded(dto.getIsExcluded());
      }
      postAccessRepository.save(pa);
    }

    dto.getUserIds().forEach(uid -> postAccessEventProducer.publish(
        PostAccessEvent.builder()
            .type(PostAccessEvent.Type.UPSERT)
            .postId(postId)
            .userId(uid)
            .isExcluded(dto.getIsExcluded())
            .build()));
  }

  @Transactional
  public void softDeleteAccess(String accessId) {
    String by = AuthenticationHelper.getMyUsername();
    PostAccess access = postAccessRepository.findById(accessId)
        .orElseThrow(() -> new AppException(ErrorCode.ACCESS_NOT_FOUND));

    String postId = access.getPost().getPostId();
    String userId = access.getUserId();

    if (!access.isDeleted()) {
      access.markDeleted(by);
      postAccessRepository.save(access);
    }

    postAccessEventProducer.publish(
        PostAccessEvent.builder()
            .type(PostAccessEvent.Type.BULK_DELETE)
            .postId(postId)
            .userId(userId)
            .build()
    );
  }

  @Transactional
  public void softDeleteAccessByUsers(
      String postId, List<String> userIds) {
    String by = AuthenticationHelper.getMyUsername();
    List<PostAccess> list = postAccessRepository
        .findByPost_PostIdAndUserIdIn(postId, userIds);
    list.forEach(a -> {
      if (!a.isDeleted()) {
        a.markDeleted(by);
      }
    });
    if (!list.isEmpty()) {
      postAccessRepository.saveAll(list);
    }

    userIds.forEach(uid -> postAccessEventProducer.publish(
        PostAccessEvent.builder()
            .type(PostAccessEvent.Type.BULK_DELETE)
            .postId(postId)
            .userId(uid)
            .build()));
  }

  // Lấy danh sách quyền của 1 bài post
  @Transactional
  public PageResponse<PostAccessResponseDto> getAccessByPostId(
      String postId,
      int page, int size) {

    Page<PostAccess> pageData = postAccessRepository.findByPost_PostId(
        postId, PageRequest.of(Math.max(1, page) - 1, size));

    Page<PostAccessResponseDto> mapped =
        pageData.map(pa -> PostAccessResponseDto.builder()
            .postAccessId(pa.getPostAccessId())
            .postId(pa.getPost().getPostId())
            .userId(pa.getUserId())
            .isExcluded(Boolean.TRUE.equals(pa.getIsExcluded()))
            .build());

    return PageResponseUtils.toPageResponse(mapped, Math.max(1, page));
  }

}

