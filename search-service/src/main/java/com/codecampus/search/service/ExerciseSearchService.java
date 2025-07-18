package com.codecampus.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.codecampus.search.dto.request.ExerciseSearchRequest;
import com.codecampus.search.entity.ExerciseDocument;
import com.codecampus.search.helper.SearchHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
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
public class ExerciseSearchService {

    ElasticsearchOperations elasticsearchOperations;

    public Page<ExerciseDocument> searchExercise(
            ExerciseSearchRequest request) {

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
                        q -> q.bool(builder -> buildQuery(request, builder)))
                .withPageable(PageRequest.of(request.page(), request.size()))
                .withHighlightQuery(highlightQuery)
                .build();

        SearchHits<ExerciseDocument> hits =
                elasticsearchOperations.search(query, ExerciseDocument.class);
        return SearchHitSupport.searchPageFor(hits, query.getPageable())
                .map(SearchHit::getContent);
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
            BoolQuery.Builder builder) {
        /* -------- full‑text Q -------- */
        if (SearchHelper.hasText(request.q())) {
            builder.must(m -> m.multiMatch(mm -> mm
                    .query(request.q())
                    .fields("title", "description")
                    .operator(Operator.And)));
        }

        /* -------- filter -------- */
        SearchHelper.addTermsFilter(builder, "tags",
                request.tags());
        SearchHelper.addTermFilter(builder, "difficulty",
                request.difficulty());
        SearchHelper.addTermFilter(builder, "exerciseType",
                request.exerciseType());
        SearchHelper.addTermFilter(builder, "created_by",
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

        builder.mustNot(mn -> mn.exists(e -> e.field("deletedAt")));

        return builder;
    }
}
