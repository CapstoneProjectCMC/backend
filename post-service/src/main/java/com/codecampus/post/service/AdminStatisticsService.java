package com.codecampus.post.service;

import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.response.stats.AdminPostStatDto;
import com.codecampus.post.dto.response.stats.HashtagStatDto;
import com.codecampus.post.dto.response.stats.UserPostLeaderboardDto;
import com.codecampus.post.repository.PostStatisticsRepository;
import com.codecampus.post.repository.projection.AdminPostStatRow;
import com.codecampus.post.repository.projection.HashtagStatRow;
import com.codecampus.post.repository.projection.UserPostLeaderboardRow;
import com.codecampus.post.utils.PageResponseUtils;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminStatisticsService {

  PostStatisticsRepository repo;

  // Bảng tổng quan
  public PageResponse<AdminPostStatDto> overview(
      String orgId, String userId, String postType, Boolean isPublic,
      Instant start, Instant end, String q,
      int page, int size, Sort sort) {

    Pageable pageable = PageRequest.of(Math.max(1, page) - 1, size,
        sort == null ? Sort.by("createdAt").descending() : sort);
    Page<AdminPostStatRow> pg =
        repo.adminPostOverview(orgId, userId, postType, isPublic, start, end, q,
            pageable);

    Page<AdminPostStatDto> mapped = pg.map(r -> AdminPostStatDto.builder()
        .postId(r.getPostId())
        .title(r.getTitle())
        .userId(r.getUserId())
        .orgId(r.getOrgId())
        .createdAt(r.getCreatedAt())
        .lastActivityAt(r.getLastActivityAt())
        .commentCount(r.getCommentCount() == null ? 0 : r.getCommentCount())
        .upvoteCount(r.getUpvoteCount() == null ? 0 : r.getUpvoteCount())
        .downvoteCount(r.getDownvoteCount() == null ? 0 : r.getDownvoteCount())
        .score((r.getUpvoteCount() == null ? 0 : r.getUpvoteCount())
            - (r.getDownvoteCount() == null ? 0 : r.getDownvoteCount()))
        .build());

    return PageResponseUtils.toPageResponse(mapped, Math.max(1, page));
  }

  // Top bình luận
  public PageResponse<AdminPostStatDto> topCommented(
      String orgId, Instant start, Instant end, int page, int size) {

    Pageable pageable = PageRequest.of(Math.max(1, page) - 1, size);
    Page<AdminPostStatRow> pg = repo.topCommented(orgId, start, end, pageable);

    Page<AdminPostStatDto> mapped = pg.map(r -> AdminPostStatDto.builder()
        .postId(r.getPostId())
        .title(r.getTitle())
        .userId(r.getUserId())
        .orgId(r.getOrgId())
        .createdAt(r.getCreatedAt())
        .lastActivityAt(r.getLastActivityAt())
        .commentCount(r.getCommentCount() == null ? 0 : r.getCommentCount())
        .upvoteCount(0)
        .downvoteCount(0)
        .score(r.getCommentCount() == null ? 0 : r.getCommentCount())
        .build());

    return PageResponseUtils.toPageResponse(mapped, Math.max(1, page));
  }

  // Top reaction
  public PageResponse<AdminPostStatDto> topReacted(
      String orgId, Instant start, Instant end, int page, int size) {

    Pageable pageable = PageRequest.of(Math.max(1, page) - 1, size);
    Page<AdminPostStatRow> pg = repo.topReacted(orgId, start, end, pageable);

    Page<AdminPostStatDto> mapped = pg.map(r -> {
      long up = r.getUpvoteCount() == null ? 0 : r.getUpvoteCount();
      long down = r.getDownvoteCount() == null ? 0 : r.getDownvoteCount();
      return AdminPostStatDto.builder()
          .postId(r.getPostId())
          .title(r.getTitle())
          .userId(r.getUserId())
          .orgId(r.getOrgId())
          .createdAt(r.getCreatedAt())
          .lastActivityAt(r.getLastActivityAt())
          .commentCount(0)
          .upvoteCount(up)
          .downvoteCount(down)
          .score(up - down)
          .build();
    });

    return PageResponseUtils.toPageResponse(mapped, Math.max(1, page));
  }

  // BXH người dùng
  public PageResponse<UserPostLeaderboardDto> topPosters(
      Instant start, Instant end, int page, int size) {

    Pageable pageable = PageRequest.of(Math.max(1, page) - 1, size);
    Page<UserPostLeaderboardRow> pg = repo.topPosters(start, end, pageable);

    Page<UserPostLeaderboardDto> mapped =
        pg.map(r -> UserPostLeaderboardDto.builder()
            .userId(r.getUserId())
            .postCount(r.getPostCount() == null ? 0 : r.getPostCount())
            .commentCount(r.getCommentCount() == null ? 0 : r.getCommentCount())
            .reactionCount(
                r.getReactionCount() == null ? 0 : r.getReactionCount())
            .build());

    return PageResponseUtils.toPageResponse(mapped, Math.max(1, page));
  }

  // Thống kê hashtag
  public PageResponse<HashtagStatDto> hashtagStats(
      String orgId, Instant start, Instant end, int page, int size) {

    Pageable pageable = PageRequest.of(Math.max(1, page) - 1, size);
    Page<HashtagStatRow> pg = repo.hashtagStats(orgId, start, end, pageable);

    Page<HashtagStatDto> mapped = pg.map(r -> HashtagStatDto.builder()
        .tag(r.getTag())
        .postCount(r.getPostCount() == null ? 0 : r.getPostCount())
        .build());

    return PageResponseUtils.toPageResponse(mapped, Math.max(1, page));
  }
}