package com.codecampus.search.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.request.ExerciseSearchRequest;
import com.codecampus.search.dto.response.ExerciseSearchResponse;
import com.codecampus.search.entity.ExerciseDocument;
import com.codecampus.search.helper.AuthenticationHelper;
import com.codecampus.search.helper.SearchHelper;
import com.codecampus.search.mapper.ExerciseMapper;
import com.codecampus.search.service.cache.UserBulkLoader;
import dtos.UserSummary;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
public class ExerciseSearchService {

  ElasticsearchOperations elasticsearchOperations;
  ExerciseMapper exerciseMapper;
  UserBulkLoader userBulkLoader;

  public PageResponse<ExerciseSearchResponse> searchExercise(
      ExerciseSearchRequest request) {

    String viewerOrgId = AuthenticationHelper.getOrgId();
    String viewerUserId = AuthenticationHelper.getUserId();

    HighlightQuery
        highlightQuery = buildHighLightQuery();

    /* -------- truy vấn -------- */
    // Như này sẽ bị lỗi vì NativeQueryBuilder().withHighlightQuery
    // không nhận HighlightBuilder
    //        .withHighlightQuery(new HighlightQuery(
    //        new HighlightBuilder()
    //                .field("title").fragmentSize(150)
    //                .field("description").fragmentSize(150),
    //        ExerciseDocument.class))
    //        .build();
    NativeQuery query = new NativeQueryBuilder()
        .withQuery(
            q -> q.bool(builder -> buildQuery(request, viewerOrgId, builder)))
        .withPageable(
            PageRequest.of(
                request.page() - 1,
                request.size(),
                Sort.by(Sort.Order.desc("createdAt"))
            ))
        .withHighlightQuery(highlightQuery)
        .build();

    SearchHits<ExerciseDocument> hits =
        elasticsearchOperations.search(query, ExerciseDocument.class);
    SearchPage<ExerciseDocument> page =
        SearchHitSupport.searchPageFor(hits, query.getPageable());

    // Nếu không muốn dùng SearchPage mà muốn dùng Page thì như này
    // var docPage = SearchHitSupport.unwrapSearchHits(page);

    /* ==== Bulk load UserSummary ==== */
    Set<String> userIds = page.getContent().stream()
        .map(SearchHit::getContent)
        .map(ExerciseDocument::getUserId)
        .collect(Collectors.toSet());

    Map<String, UserSummary> summaries = userBulkLoader.loadAll(userIds);

    List<ExerciseSearchResponse> data = page.getContent().stream()
        .map(SearchHit::getContent)
        .map(doc -> {
          ExerciseSearchResponse base =
              exerciseMapper.toExerciseSearchResponseFromExerciseDocument(
                  doc);

          boolean purchased = isPurchased(viewerUserId, viewerOrgId, doc);

          return base.toBuilder()
              .user(summaries.get(doc.getUserId()))
              .purchased(purchased)
              .build();
        })
        .toList();

//        return SearchHitSupport.searchPageFor(hits, query.getPageable())
//                .map(SearchHit::getContent);

    return PageResponse.<ExerciseSearchResponse>builder()
        .currentPage(request.page())
        .pageSize(page.getSize())
        .totalPages(page.getTotalPages())
        .totalElements(page.getTotalElements())
        .data(data)
        .build();
  }

  HighlightQuery buildHighLightQuery() {
    Highlight highlight = new Highlight(
        List.of(
            new HighlightField(
                "title",
                HighlightFieldParameters.builder()
                    .withFragmentSize(150)
                    .build()),
            new HighlightField(
                "description",
                HighlightFieldParameters.builder()
                    .withFragmentSize(150)
                    .build())
        )
    );

    return new HighlightQuery(highlight, ExerciseDocument.class);
  }

  BoolQuery.Builder buildQuery(
      ExerciseSearchRequest request,
      String viewerOrgId,
      BoolQuery.Builder builder) {

    /* -------- full‑text Q -------- */
    if (SearchHelper.hasText(request.q())) {

      // should: exact equals trên subfield .keyword (ưu tiên cao nhất)
      final String q = request.q();
      builder.should(s -> s.term(t -> t.field("title.keyword").value(q)))
          .boost(1.0f);
      builder.should(
          s -> s.term(t -> t.field("description.keyword").value(q)));

      // should: exact phrase (ưu tiên cao)
      builder.should(s -> s.matchPhrase(
          mp -> mp.field("title").query(q).slop(0).boost(5.0f)));
      builder.should(s -> s.matchPhrase(
          mp -> mp.field("description").query(q).slop(0)
              .boost(4.0f)));

      // should: fuzzy/contains đa trường (ưu tiên thấp hơn)
      builder.should(s -> s.multiMatch(mm -> mm
          .query(q)
          .fields("title^3", "description", "tags.search^2",
              "exerciseType.search")
          .operator(Operator.And)
          .fuzziness("AUTO")
          .prefixLength(1)
          .boost(2.0f)));

      builder.minimumShouldMatch("1");
    }

    /* -------- filter -------- */
    SearchHelper.addTermsFilter(builder, "tags",
        request.tags());
    SearchHelper.addTermFilter(builder, "difficulty",
        request.difficulty());
    SearchHelper.addTermFilter(builder, "exerciseType",
        request.exerciseType());
    SearchHelper.addTermFilter(builder, "createdBy",
        request.createdBy());
    SearchHelper.addTermFilter(builder, "orgId",
        request.orgId());
    SearchHelper.addTermFilter(builder, "freeForOrg",
        request.freeForOrg());
    SearchHelper.addTermFilter(builder, "allowAiQuestion",
        request.allowAiQuestion());

    SearchHelper.addRangeFilter(builder, "cost",
        request.minCost(),
        request.maxCost());
    SearchHelper.addRangeFilter(builder, "startTime",
        request.startAfter(),
        null);
    SearchHelper.addRangeFilter(builder, "endTime", null,
        request.endBefore());

    // Không trả item đã xoá mềm
    builder.mustNot(mn -> mn.exists(e -> e.field("deletedAt")));

    // visible nếu (visibility=true) OR (orgId = viewerOrgId)
    builder.filter(f -> f.bool(bv -> {
      bv.should(s -> s.term(t -> t.field("visibility").value(true)));
      if (viewerOrgId != null) {
        bv.should(s -> s.terms(ts -> ts.field("orgId").terms(tv -> tv.value(
            java.util.List.of(FieldValue.of(viewerOrgId))
        ))));
      }
      return bv;
    }));

    return builder;
  }

  private boolean isPurchased(String viewerUserId, String viewerOrgId,
                              ExerciseDocument doc) {
    // 1) Chủ bài/author luôn có quyền (coi như purchased)
    if (viewerUserId != null && viewerUserId.equals(doc.getUserId())) {
      return true;
    }
    // 2) Bài miễn phí (cost=0 hoặc null)
    if (doc.getCost() == null || doc.getCost() <= 0.0) {
      return true;
    }
    // 3) Free cho org và viewer thuộc đúng org
    if (Boolean.TRUE.equals(doc.getFreeForOrg())
        && viewerOrgId != null
        && viewerOrgId.equals(doc.getOrgId())) {
      return true;
    }
    // 4) Đã mua theo event (buyerUserIds chứa viewer)
    return viewerUserId != null
        && doc.getBuyerUserIds() != null
        && doc.getBuyerUserIds().contains(viewerUserId);
  }
}
