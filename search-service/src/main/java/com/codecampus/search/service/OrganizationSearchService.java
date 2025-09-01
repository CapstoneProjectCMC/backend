package com.codecampus.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.response.OrganizationSearchResponse;
import com.codecampus.search.entity.OrganizationDocument;
import com.codecampus.search.helper.SearchHelper;
import com.codecampus.search.repository.client.OrganizationClient;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationSearchService {

  ElasticsearchOperations es;
  OrganizationClient organizationClient;

  public PageResponse<OrganizationSearchResponse> search(
      String q, String status,
      int page, int size,
      boolean includeBlocks,
      int blocksPage, int blocksSize,
      int membersPage, int membersSize,
      boolean activeOnlyMembers, boolean includeUnassigned) {

    NativeQuery query = new NativeQueryBuilder()
        .withQuery(qb -> qb.bool(b -> buildQuery(q, status, b)))
        .withPageable(PageRequest.of(Math.max(1, page) - 1, size,
            Sort.by(Sort.Order.desc("createdAt"))))
        .build();

    SearchHits<OrganizationDocument> hits =
        es.search(query, OrganizationDocument.class);
    SearchPage<OrganizationDocument> sp =
        SearchHitSupport.searchPageFor(hits, query.getPageable());

    List<OrganizationSearchResponse> data = sp.getContent().stream()
        .map(SearchHit::getContent)
        .map(doc -> {
          var res = OrganizationSearchResponse.builder()
              .id(doc.getId())
              .name(doc.getName())
              .description(doc.getDescription())
              .logoUrl(doc.getLogoUrl())
              .email(doc.getEmail())
              .phone(doc.getPhone())
              .address(doc.getAddress())
              .status(doc.getStatus())
              .createdAt(doc.getCreatedAt())
              .updatedAt(doc.getUpdatedAt())
              .build();

          if (includeBlocks) {
            var api = organizationClient.getBlocksOfOrg(
                doc.getId(), blocksPage, blocksSize, membersPage, membersSize,
                activeOnlyMembers, includeUnassigned);
            res.setBlocks(api != null ? api.getResult() : null);
          }
          return res;
        }).toList();

    return PageResponse.<OrganizationSearchResponse>builder()
        .currentPage(Math.max(1, page))
        .pageSize(sp.getSize())
        .totalPages(sp.getTotalPages())
        .totalElements(sp.getTotalElements())
        .data(data)
        .build();
  }

  private BoolQuery.Builder buildQuery(String q, String status,
                                       BoolQuery.Builder b) {
    if (SearchHelper.hasText(q)) {
      b.should(s -> s.term(t -> t.field("name.keyword").value(q))).boost(1.0f);
      b.should(s -> s.matchPhrase(
          mp -> mp.field("name").query(q).slop(0).boost(5.0f)));
      b.should(s -> s.matchPhrase(
          mp -> mp.field("description").query(q).slop(0).boost(3.0f)));
      b.should(s -> s.multiMatch(mm -> mm.query(q)
          .fields("name^3", "description^2", "address", "email", "phone",
              "status.search")
          .operator(Operator.And)
          .fuzziness("AUTO")
          .prefixLength(1)
          .boost(2.0f)));
      b.minimumShouldMatch("1");
    }

    SearchHelper.addTermFilter(b, "status", status);
    b.mustNot(mn -> mn.exists(e -> e.field("deletedAt")));
    return b;
  }
}