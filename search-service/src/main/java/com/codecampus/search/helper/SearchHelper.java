package com.codecampus.search.helper;

import static java.util.stream.Collectors.toList;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchHelper {

  public <T> void addTermFilter(
      BoolQuery.Builder bool,
      String field,
      T value) {

    if (value == null) {
      return;
    }

    if (value instanceof CharSequence cs && cs.toString().trim().isEmpty()) {
      return;
    }

    bool.filter(q -> q.term(t -> t
        .field(field)
        .value(asFieldValue(value))));
  }


  public void addTermsFilter(
      BoolQuery.Builder bool,
      String field,
      Set<String> values) {

    if (values == null || values.isEmpty()) {
      return;
    }

    bool.filter(q -> q.terms(t -> t
        .field(field)
        .terms(ts -> ts.value(
            values.stream()
                .map(SearchHelper::asFieldValue)
                .collect(toList())
        ))));
  }

  public void addRangeFilter(BoolQuery.Builder bool,
                             String field,
                             Object gte,
                             Object lte) {

    if (gte == null && lte == null) {
      return;
    }

    bool.filter(q -> q.range(r -> {

      /* ===== Range cho SỐ ===== */
      if (gte instanceof Number || lte instanceof Number) {
        return r.number(n -> {
          n.field(field);
          if (gte != null) {
            n.gte(((Number) gte).doubleValue());
          }
          if (lte != null) {
            n.lte(((Number) lte).doubleValue());
          }
          return n;
        });
      }

      /* ===== Range cho NGÀY/GIỜ ===== */
      return r.date(d -> {
        d.field(field);
        if (gte != null) {
          d.gte(toIsoString(gte));
        }
        if (lte != null) {
          d.lte(toIsoString(lte));
        }
        return d;
      });
    }));
  }

  public boolean hasText(String string) {
    return string != null && !string.trim().isEmpty();
  }

  private String toIsoString(Object value) {
    return value instanceof Instant i
        ? DateTimeFormatter.ISO_INSTANT.format(i)
        : value.toString();
  }

  public FieldValue asFieldValue(Object value) {
    return switch (value) {
      case null -> null;
      case String s -> FieldValue.of(s);
      case Integer i -> FieldValue.of(i);
      case Long i -> FieldValue.of(i);
      case Double i -> FieldValue.of(i);
      case Float i -> FieldValue.of(i);
      case Boolean i -> FieldValue.of(i);
      case Instant t -> FieldValue.of(DateTimeFormatter.ISO_INSTANT.format(t));
      default -> FieldValue.of(value.toString());
    };
  }
}
