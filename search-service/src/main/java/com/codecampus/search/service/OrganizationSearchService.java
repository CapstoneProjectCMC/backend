package com.codecampus.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.response.BlockWithMembersPageResponse;
import com.codecampus.search.dto.response.BlockWithMembersWithUserPageResponse;
import com.codecampus.search.dto.response.MemberInBlockResponse;
import com.codecampus.search.dto.response.MemberInBlockWithUserResponse;
import com.codecampus.search.dto.response.OrganizationSearchResponse;
import com.codecampus.search.entity.OrganizationDocument;
import com.codecampus.search.helper.SearchHelper;
import com.codecampus.search.repository.client.OrganizationClient;
import com.codecampus.search.service.cache.UserBulkLoader;
import dtos.UserSummary;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  UserBulkLoader userBulkLoader;

  public PageResponse<OrganizationSearchResponse> search(
      String q, String status,
      int page, int size,
      boolean includeBlocks,
      int blocksPage, int blocksSize,
      int membersPage, int membersSize,
      boolean activeOnlyMembers, boolean includeUnassigned) {

    boolean hasQ = SearchHelper.hasText(q);
    boolean hasStatus = SearchHelper.hasText(status);

    NativeQueryBuilder nqb = new NativeQueryBuilder()
        .withPageable(PageRequest.of(Math.max(1, page) - 1, size,
            Sort.by(Sort.Order.desc("createdAt"))));

    if (!hasQ && !hasStatus) {
      nqb.withQuery(qb -> qb.bool(b -> {
        b.must(m -> m.matchAll(ma -> ma));
        b.mustNot(mn -> mn.exists(e -> e.field("deletedAt")));
        return b;
      }));
    } else {
      nqb.withQuery(qb -> qb.bool(b -> buildQuery(q, status, b)));
    }

    NativeQuery query = nqb.build();

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
              .ownerId(doc.getOwnerId())
              .logoUrl(doc.getLogoUrl())
              .email(doc.getEmail())
              .phone(doc.getPhone())
              .address(doc.getAddress())
              .status(doc.getStatus())
              .createdAt(doc.getCreatedAt())
              .updatedAt(doc.getUpdatedAt())
              .build();

          if (includeBlocks) {
            var api = organizationClient.internalGetBlocksOfOrg(
                doc.getId(), blocksPage, blocksSize, membersPage, membersSize,
                activeOnlyMembers, includeUnassigned);

            PageResponse<BlockWithMembersPageResponse> raw =
                (api != null) ? api.getResult() : null;

            if (raw != null && raw.getData() != null) {
              // 1) Thu thập toàn bộ userId của tất cả block trên trang này
              Set<String> userIds = raw.getData().stream()
                  .flatMap(b -> {
                    var mp = b.getMembers();
                    if (mp == null || mp.getData() == null) {
                      return Stream.empty();
                    }
                    return mp.getData().stream();
                  })
                  .map(MemberInBlockResponse::getUserId)
                  .filter(Objects::nonNull)
                  .collect(Collectors.toSet());

              // 2) Bulk load UserSummary từ cache
              Map<String, UserSummary> userMap =
                  userBulkLoader.loadAll(userIds);

              // 3) Map sang DTO có UserSummary
              List<BlockWithMembersWithUserPageResponse> newBlocks =
                  raw.getData().stream()
                      .map(b -> {
                        PageResponse<MemberInBlockWithUserResponse> members =
                            (b.getMembers() == null)
                                ?
                                PageResponse.<MemberInBlockWithUserResponse>builder()
                                    .build()
                                :
                                PageResponse.<MemberInBlockWithUserResponse>builder()
                                    .currentPage(
                                        b.getMembers().getCurrentPage())
                                    .totalPages(b.getMembers().getTotalPages())
                                    .pageSize(b.getMembers().getPageSize())
                                    .totalElements(
                                        b.getMembers().getTotalElements())
                                    .data(
                                        (b.getMembers().getData() == null) ?
                                            List.of() :
                                            b.getMembers().getData().stream()
                                                .map(
                                                    m -> MemberInBlockWithUserResponse.builder()
                                                        .user(userMap.get(
                                                            m.getUserId())) // <--- thay userId bằng cache
                                                        .role(m.getRole())
                                                        .active(m.isActive())
                                                        .build())
                                                .toList()
                                    )
                                    .build();

                        return BlockWithMembersWithUserPageResponse.builder()
                            .id(b.getId())
                            .orgId(b.getOrgId())
                            .name(b.getName())
                            .code(b.getCode())
                            .description(b.getDescription())
                            .createdAt(b.getCreatedAt())
                            .updatedAt(b.getUpdatedAt())
                            .members(members)
                            .build();
                      })
                      .toList();

              res.setBlocks(
                  PageResponse.<BlockWithMembersWithUserPageResponse>builder()
                      .currentPage(raw.getCurrentPage())
                      .totalPages(raw.getTotalPages())
                      .pageSize(raw.getPageSize())
                      .totalElements(raw.getTotalElements())
                      .data(newBlocks)
                      .build());
            }
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