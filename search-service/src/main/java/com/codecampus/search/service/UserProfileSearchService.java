package com.codecampus.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.request.UserProfileSearchRequest;
import com.codecampus.search.dto.response.UserProfileResponse;
import com.codecampus.search.entity.UserProfileDocument;
import com.codecampus.search.helper.SearchHelper;
import com.codecampus.search.mapper.UserProfileMapper;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileSearchService {

    ElasticsearchOperations elasticsearchOperations;
    UserProfileMapper userProfileMapper;

    public PageResponse<UserProfileResponse> search(
            UserProfileSearchRequest r) {

        HighlightQuery highlight = buildHighlight();

        NativeQueryBuilder qb = new NativeQueryBuilder()
                .withPageable(PageRequest.of(r.page() - 1, r.size()))
                .withHighlightQuery(highlight);

        // Nếu không có filter nào -> match_all để cho phép để trống param
        boolean hasAny =
                SearchHelper.hasText(r.q()) ||
                        SearchHelper.hasText(r.userId()) ||
                        SearchHelper.hasText(r.username()) ||
                        SearchHelper.hasText(r.email()) ||
                        (r.roles() != null && !r.roles().isEmpty()) ||
                        r.active() != null || r.gender() != null ||
                        SearchHelper.hasText(r.city()) ||
                        r.educationMin() != null || r.educationMax() != null ||
                        r.createdAfter() != null || r.createdBefore() != null;

        if (!hasAny) {
            qb.withQuery(q -> q.matchAll(m -> m));
        } else {
            qb.withQuery(q -> q.bool(b -> buildQuery(r, b)));
        }

        NativeQuery query = qb.build();

        SearchHits<UserProfileDocument> hits =
                elasticsearchOperations.search(query,
                        UserProfileDocument.class);
        SearchPage<UserProfileDocument> page =
                SearchHitSupport.searchPageFor(hits, query.getPageable());

        List<UserProfileResponse> data = page.getContent().stream()
                .map(SearchHit::getContent)
                .map(userProfileMapper::toUserProfileResponseFromUserProfileDocument)
                .toList();

        return PageResponse.<UserProfileResponse>builder()
                .currentPage(r.page())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .data(data)
                .build();
    }

    BoolQuery.Builder buildQuery(UserProfileSearchRequest r,
                                 BoolQuery.Builder b) {
        // ===== Full-text (relative + absolute) =====
        if (SearchHelper.hasText(r.q())) {
            String q = r.q();

            // exact equals ưu tiên cao
            b.should(s -> s.term(t -> t.field("username").value(q)))
                    .boost(1.0f);
            b.should(s -> s.term(t -> t.field("email").value(q)));

            // exact phrase (name fields)
            b.should(s -> s.matchPhrase(
                    mp -> mp.field("displayName").query(q).slop(0)
                            .boost(5.0f)));
            b.should(s -> s.matchPhrase(
                    mp -> mp.field("firstName").query(q).slop(0).boost(4.0f)));
            b.should(s -> s.matchPhrase(
                    mp -> mp.field("lastName").query(q).slop(0).boost(4.0f)));
            b.should(s -> s.matchPhrase(
                    mp -> mp.field("city").query(q).slop(0).boost(2.0f)));

            // fuzzy / contains across many fields
            b.should(s -> s.multiMatch(mm -> mm
                    .query(q)
                    .fields("displayName^3", "firstName^2", "lastName^2",
                            "username.search^2", "email.search", "bio", "city")
                    .operator(Operator.And)
                    .fuzziness("AUTO")
                    .prefixLength(1)
                    .boost(2.0f)));
            b.minimumShouldMatch("1");
        }

        // ===== Exact filters (bỏ qua nếu null/empty) =====
        SearchHelper.addTermFilter(b, "userId", r.userId());
        SearchHelper.addTermFilter(b, "username", r.username());
        SearchHelper.addTermFilter(b, "email", r.email());
        SearchHelper.addTermsFilter(b, "roles", r.roles());
        SearchHelper.addTermFilter(b, "active", r.active());
        SearchHelper.addTermFilter(b, "gender", r.gender());
        if (SearchHelper.hasText(r.city())) {
            // cho exact city khi có param
            SearchHelper.addTermFilter(b, "city.keyword", r.city());
        }

        // ===== Range filters =====
        SearchHelper.addRangeFilter(b, "education", r.educationMin(),
                r.educationMax());
        SearchHelper.addRangeFilter(b, "createdAt", r.createdAfter(),
                r.createdBefore());

        return b;
    }

    HighlightQuery buildHighlight() {
        Highlight h = new Highlight(List.of(
                new HighlightField("displayName",
                        HighlightFieldParameters.builder().withFragmentSize(150)
                                .build()),
                new HighlightField("firstName",
                        HighlightFieldParameters.builder().withFragmentSize(150)
                                .build()),
                new HighlightField("lastName",
                        HighlightFieldParameters.builder().withFragmentSize(150)
                                .build()),
                new HighlightField("bio",
                        HighlightFieldParameters.builder().withFragmentSize(150)
                                .build())
        ));
        return new HighlightQuery(h, UserProfileDocument.class);
    }

}
