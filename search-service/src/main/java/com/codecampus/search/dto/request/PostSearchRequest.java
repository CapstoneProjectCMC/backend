package com.codecampus.search.dto.request;

public record PostSearchRequest(
    String q,
    String orgId,
    String postType,
    Boolean isPublic,
    String status,
    int page,
    int size
) {
}