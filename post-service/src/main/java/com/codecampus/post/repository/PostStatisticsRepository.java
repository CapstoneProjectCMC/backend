package com.codecampus.post.repository;

import com.codecampus.post.entity.Post;
import com.codecampus.post.repository.projection.AdminPostStatRow;
import com.codecampus.post.repository.projection.HashtagStatRow;
import com.codecampus.post.repository.projection.UserPostLeaderboardRow;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostStatisticsRepository
    extends JpaRepository<Post, String> {
  // Bảng tổng quan bài viết (kèm số liệu)
  @Query(
      value = """
          SELECT
            p.post_id        AS postId,
            p.title          AS title,
            p.user_id        AS userId,
            p.org_id         AS orgId,
            p.created_at     AS createdAt,
            GREATEST(
              COALESCE(MAX(p.updated_at), p.created_at),
              COALESCE(MAX(c.created_at), '1970-01-01'::timestamp),
              COALESCE(MAX(r.created_at), '1970-01-01'::timestamp)
            )                 AS lastActivityAt,
            COALESCE(COUNT(DISTINCT c.comment_id), 0)                                          AS commentCount,
            COALESCE(SUM(CASE WHEN r.emoji_type = 'upvote'   THEN 1 ELSE 0 END), 0)            AS upvoteCount,
            COALESCE(SUM(CASE WHEN r.emoji_type = 'downvote' THEN 1 ELSE 0 END), 0)            AS downvoteCount
          FROM post p
          LEFT JOIN post_comment  c ON c.post_id = p.post_id AND c.deleted_at IS NULL
          LEFT JOIN post_reaction r ON r.post_id = p.post_id AND r.deleted_at IS NULL
          WHERE p.deleted_at IS NULL
            AND (:orgId   IS NULL OR p.org_id = :orgId)
            AND (:userId  IS NULL OR p.user_id = :userId)
            AND (:postType IS NULL OR lower(p.post_type) = lower(:postType))
            AND (:isPublic IS NULL OR p.is_public = :isPublic)
            AND (:start   IS NULL OR p.created_at >= :start)
            AND (:end     IS NULL OR p.created_at <  :end)
            AND (:q IS NULL OR p.search_vector @@ plainto_tsquery('simple', :q))
          GROUP BY p.post_id, p.title, p.user_id, p.org_id, p.created_at
          """,
      countQuery = """
          SELECT COUNT(*) FROM (
            SELECT p.post_id
            FROM post p
            WHERE p.deleted_at IS NULL
              AND (:orgId   IS NULL OR p.org_id = :orgId)
              AND (:userId  IS NULL OR p.user_id = :userId)
              AND (:postType IS NULL OR lower(p.post_type) = lower(:postType))
              AND (:isPublic IS NULL OR p.is_public = :isPublic)
              AND (:start   IS NULL OR p.created_at >= :start)
              AND (:end     IS NULL OR p.created_at <  :end)
              AND (:q IS NULL OR p.search_vector @@ plainto_tsquery('simple', :q))
          ) x
          """,
      nativeQuery = true
  )
  Page<AdminPostStatRow> adminPostOverview(
      @Param("orgId") String orgId,
      @Param("userId") String userId,
      @Param("postType") String postType,
      @Param("isPublic") Boolean isPublic,
      @Param("start") Instant start,
      @Param("end") Instant end,
      @Param("q") String q,
      Pageable pageable);

  // Top bài viết theo số bình luận trong khoảng thời gian
  @Query(
      value = """
          SELECT
            p.post_id    AS postId,
            p.title      AS title,
            p.user_id    AS userId,
            p.org_id     AS orgId,
            p.created_at AS createdAt,
            COALESCE(MAX(p.updated_at), p.created_at) AS lastActivityAt,
            COALESCE(COUNT(c.comment_id), 0)          AS commentCount,
            0 AS upvoteCount,
            0 AS downvoteCount
          FROM post p
          LEFT JOIN post_comment c
            ON c.post_id = p.post_id
           AND c.deleted_at IS NULL
           AND (:start IS NULL OR c.created_at >= :start)
           AND (:end   IS NULL OR c.created_at <  :end)
          WHERE p.deleted_at IS NULL
            AND (:orgId IS NULL OR p.org_id = :orgId)
          GROUP BY p.post_id, p.title, p.user_id, p.org_id, p.created_at
          ORDER BY commentCount DESC, createdAt DESC
          """,
      countQuery = """
          SELECT COUNT(*) FROM (
            SELECT p.post_id
            FROM post p
            WHERE p.deleted_at IS NULL
              AND (:orgId IS NULL OR p.org_id = :orgId)
          ) x
          """,
      nativeQuery = true
  )
  Page<AdminPostStatRow> topCommented(
      @Param("orgId") String orgId,
      @Param("start") Instant start,
      @Param("end") Instant end,
      Pageable pageable);

  // Top bài viết theo reaction (score = up - down) trong khoảng thời gian
  @Query(
      value = """
          SELECT
            p.post_id    AS postId,
            p.title      AS title,
            p.user_id    AS userId,
            p.org_id     AS orgId,
            p.created_at AS createdAt,
            COALESCE(MAX(p.updated_at), p.created_at) AS lastActivityAt,
            0 AS commentCount,
            COALESCE(SUM(CASE WHEN r.emoji_type = 'upvote'   THEN 1 ELSE 0 END), 0) AS upvoteCount,
            COALESCE(SUM(CASE WHEN r.emoji_type = 'downvote' THEN 1 ELSE 0 END), 0) AS downvoteCount
          FROM post p
          LEFT JOIN post_reaction r
            ON r.post_id = p.post_id
           AND r.deleted_at IS NULL
           AND (:start IS NULL OR r.created_at >= :start)
           AND (:end   IS NULL OR r.created_at <  :end)
          WHERE p.deleted_at IS NULL
            AND (:orgId IS NULL OR p.org_id = :orgId)
          GROUP BY p.post_id, p.title, p.user_id, p.org_id, p.created_at
          ORDER BY (COALESCE(SUM(CASE WHEN r.emoji_type = 'upvote' THEN 1 ELSE 0 END),0)
                  - COALESCE(SUM(CASE WHEN r.emoji_type = 'downvote' THEN 1 ELSE 0 END),0)) DESC,
                   createdAt DESC
          """,
      countQuery = """
          SELECT COUNT(*) FROM (
            SELECT p.post_id
            FROM post p
            WHERE p.deleted_at IS NULL
              AND (:orgId IS NULL OR p.org_id = :orgId)
          ) x
          """,
      nativeQuery = true
  )
  Page<AdminPostStatRow> topReacted(
      @Param("orgId") String orgId,
      @Param("start") Instant start,
      @Param("end") Instant end,
      Pageable pageable);

  // Bảng xếp hạng người dùng theo số bài viết (kèm tổng comment & reaction đã tạo)
  @Query(
      value = """
          SELECT
            u.user_id     AS userId,
            COALESCE(COUNT(DISTINCT p.post_id), 0) AS postCount,
            COALESCE(COUNT(DISTINCT c.comment_id), 0) AS commentCount,
            COALESCE(COUNT(DISTINCT r.reaction_id), 0) AS reactionCount
          FROM (
            SELECT DISTINCT user_id FROM post WHERE deleted_at IS NULL
            UNION
            SELECT DISTINCT user_id FROM post_comment WHERE deleted_at IS NULL
            UNION
            SELECT DISTINCT user_id FROM post_reaction WHERE deleted_at IS NULL
          ) u
          LEFT JOIN post p
            ON p.user_id = u.user_id
           AND p.deleted_at IS NULL
           AND (:start IS NULL OR p.created_at >= :start)
           AND (:end   IS NULL OR p.created_at <  :end)
          LEFT JOIN post_comment c
            ON c.user_id = u.user_id
           AND c.deleted_at IS NULL
           AND (:start IS NULL OR c.created_at >= :start)
           AND (:end   IS NULL OR c.created_at <  :end)
          LEFT JOIN post_reaction r
            ON r.user_id = u.user_id
           AND r.deleted_at IS NULL
           AND (:start IS NULL OR r.created_at >= :start)
           AND (:end   IS NULL OR r.created_at <  :end)
          GROUP BY u.user_id
          ORDER BY postCount DESC, commentCount DESC
          """,
      countQuery = """
          SELECT COUNT(*) FROM (
            SELECT DISTINCT user_id FROM post WHERE deleted_at IS NULL
            UNION
            SELECT DISTINCT user_id FROM post_comment WHERE deleted_at IS NULL
            UNION
            SELECT DISTINCT user_id FROM post_reaction WHERE deleted_at IS NULL
          ) x
          """,
      nativeQuery = true
  )
  Page<UserPostLeaderboardRow> topPosters(
      @Param("start") Instant start,
      @Param("end") Instant end,
      Pageable pageable);

  // Thống kê hashtag (chuẩn hoá bằng regexp_split_to_table)
  @Query(
      value = """
          WITH tags AS (
            SELECT
              lower(trim(both '#' from t)) AS tag,
              p.post_id
            FROM post p,
                 LATERAL regexp_split_to_table(COALESCE(p.hashtag, ''), E'\\s+') t
            WHERE p.deleted_at IS NULL
              AND t <> ''
              AND (:orgId IS NULL OR p.org_id = :orgId)
              AND (:start IS NULL OR p.created_at >= :start)
              AND (:end   IS NULL OR p.created_at <  :end)
          )
          SELECT tag AS tag, COUNT(DISTINCT post_id) AS postCount
          FROM tags
          GROUP BY tag
          ORDER BY postCount DESC, tag ASC
          """,
      countQuery = """
          SELECT COUNT(*) FROM (
            WITH tags AS (
              SELECT lower(trim(both '#' from t)) AS tag
              FROM post p,
                   LATERAL regexp_split_to_table(COALESCE(p.hashtag, ''), E'\\s+') t
              WHERE p.deleted_at IS NULL
                AND t <> ''
                AND (:orgId IS NULL OR p.org_id = :orgId)
                AND (:start IS NULL OR p.created_at >= :start)
                AND (:end   IS NULL OR p.created_at <  :end)
            )
            SELECT DISTINCT tag FROM tags
          ) x
          """,
      nativeQuery = true
  )
  Page<HashtagStatRow> hashtagStats(
      @Param("orgId") String orgId,
      @Param("start") Instant start,
      @Param("end") Instant end,
      Pageable pageable);
}
