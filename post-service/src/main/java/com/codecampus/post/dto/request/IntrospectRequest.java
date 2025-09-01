package com.codecampus.post.dto.request;


import lombok.Builder;

@Builder
public record IntrospectRequest(String token) {
}
