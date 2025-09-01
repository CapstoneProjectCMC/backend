package com.codecampus.search.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.request.PostSearchRequest;
import com.codecampus.search.dto.response.PostSearchResponse;
import com.codecampus.search.entity.PostDocument;
import com.codecampus.search.helper.AuthenticationHelper;
import com.codecampus.search.helper.SearchHelper;
import com.codecampus.search.repository.client.PostStatsClient;
import com.codecampus.search.service.cache.UserBulkLoader;
import dtos.UserSummary;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostSearchService {

  ElasticsearchOperations es;
  UserBulkLoader userBulkLoader;
  PostStatsClient statsClient;

  public PageResponse<PostSearchResponse> search(
      PostSearchRequest r,
      String viewerId) {

    String viewerOrgId = AuthenticationHelper.getOrgId();

    HighlightQuery highlight = buildHighlight();

    NativeQuery query = new NativeQueryBuilder()
        .withQuery(q -> q.bool(b -> buildQuery(r, viewerId, viewerOrgId, b)))
        .withPageable(PageRequest.of(Math.max(1, r.page()) - 1, r.size()))
        .withHighlightQuery(highlight)
        .build();

    SearchHits<PostDocument> hits = es.search(query, PostDocument.class);
    SearchPage<PostDocument> page =
        SearchHitSupport.searchPageFor(hits, query.getPageable());

    // Bulk load user summaries
    Set<String> userIds = page.getContent().stream()
        .map(SearchHit::getContent).map(PostDocument::getUserId)
        .collect(Collectors.toSet());
    Map<String, UserSummary> userMap = userBulkLoader.loadAll(userIds);

    // Map + hydrate counts (đơn giản: gọi từng post; TODO: batch để tối ưu)
    List<PostSearchResponse> data = page.getContent().stream()
        .map(SearchHit::getContent)
        .map(doc -> {
          long comments = Optional.ofNullable(
              statsClient.commentCount(doc.getId()).getResult()).orElse(0L);
          Map<String, Long> react = Optional.ofNullable(
                  statsClient.reactionCounts(doc.getId()).getResult())
              .orElse(Map.of());
          return PostSearchResponse.builder()
              .postId(doc.getId())
              .user(userMap.get(doc.getUserId()))
              .orgId(doc.getOrgId())
              .postType(doc.getPostType())
              .title(doc.getTitle())
              .content(doc.getContent())
              .isPublic(doc.getIsPublic())
              .allowComment(doc.getAllowComment())
              .hashtag(doc.getHashtag())
              .status(doc.getStatus())
              .imagesUrls(doc.getFileUrls())
              .createdAt(
                  doc.getCreatedAt() != null ? doc.getCreatedAt().toString() :
                      null)
              .commentCount(comments)
              .upvoteCount(react.getOrDefault("upvote", 0L))
              .downvoteCount(react.getOrDefault("downvote", 0L))
              .build();
        }).toList();

    return PageResponse.<PostSearchResponse>builder()
        .currentPage(Math.max(1, r.page()))
        .pageSize(page.getSize())
        .totalPages(page.getTotalPages())
        .totalElements(page.getTotalElements())
        .data(data)
        .build();
  }

  private HighlightQuery buildHighlight() {
    Highlight h = new Highlight(List.of(
        new HighlightField("title",
            HighlightFieldParameters.builder().withFragmentSize(150).build()),
        new HighlightField("content",
            HighlightFieldParameters.builder().withFragmentSize(150).build())
    ));
    return new HighlightQuery(h, PostDocument.class);
  }

  private BoolQuery.Builder buildQuery(
      PostSearchRequest r,
      String viewerOrgId,
      String viewerId,
      BoolQuery.Builder b) {
    // full-text
    if (SearchHelper.hasText(r.q())) {
      String q = r.q();
      b.should(s -> s.term(t -> t.field("title.keyword").value(q))).boost(1.0f);
      b.should(s -> s.term(t -> t.field("content.keyword").value(q)));
      b.should(s -> s.matchPhrase(
          mp -> mp.field("title").query(q).slop(0).boost(5.0f)));
      b.should(s -> s.matchPhrase(
          mp -> mp.field("content").query(q).slop(0).boost(4.0f)));
      b.should(s -> s.multiMatch(mm -> mm.query(q)
          .fields("title^3", "content^2", "hashtag", "status", "postType")
          .operator(Operator.And).fuzziness("AUTO").prefixLength(1)
          .boost(2.0f)));
      b.minimumShouldMatch("1");
    }

    // exact filter
    SearchHelper.addTermFilter(b, "orgId", r.orgId());
    SearchHelper.addTermFilter(b, "postType", r.postType());
    SearchHelper.addTermFilter(b, "isPublic", r.isPublic());
    SearchHelper.addTermFilter(b, "status", r.status());

    // không trả item xoá mềm
    b.mustNot(mn -> mn.exists(e -> e.field("deletedAt")));

    b.filter(f -> f.bool(v -> {
      v.should(s -> s.term(t -> t.field("isPublic").value(true)));
      v.should(s -> s.term(t -> t.field("postType").value("Global")));
      if (viewerOrgId != null && !viewerOrgId.isBlank()) {
        v.should(s -> s.term(t -> t.field("orgId").value(viewerOrgId)));
      }
      return v;
    }));

    // visibility như DB: owner OR isPublic OR Global OR (allow contains viewer & not in exclude)
    b.filter(f -> f.bool(v -> {
      v.should(s -> s.term(t -> t.field("isPublic").value(true)));
      v.should(s -> s.term(t -> t.field("postType").value("Global")));
      if (viewerId != null && !viewerId.isBlank()) {
        v.should(s -> s.term(t -> t.field("userId").value(viewerId)));
        v.should(s -> s.bool(bb -> bb
            .must(m1 -> m1.terms(
                ts -> ts.field("allowUserIds").terms(tv -> tv.value(
                    List.of(FieldValue.of(viewerId))))))
            .mustNot(mn -> mn.terms(
                ts -> ts.field("excludeUserIds").terms(tv -> tv.value(
                    List.of(FieldValue.of(viewerId))))))
        ));
      }
      return v;
    }));

    return b;
  }
}