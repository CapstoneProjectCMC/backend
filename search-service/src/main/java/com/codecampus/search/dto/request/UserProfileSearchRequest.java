package com.codecampus.search.dto.request;


import java.time.Instant;
import java.util.Set;

public record UserProfileSearchRequest(
    String q,               // full-text (username/email/name/bio/city…)
    String userId,          // exact
    String username,        // exact
    String email,           // exact
    Set<String> roles,      // terms
    Boolean active,         // term
    Boolean gender,         // term
    String city,            // exact or left empty
    Integer educationMin,   // range
    Integer educationMax,   // range
    Instant createdAfter,   // range
    Instant createdBefore,  // range
    int page,
    int size
) {
}