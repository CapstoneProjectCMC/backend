package com.codecampus.submission.dto.common;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Mặc định cho phép response cả null khi Dev
// Khi build thì KHÔNG response null
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    int currentPage;
    int totalPages;
    int pageSize;
    long totalElements;

    @Builder.Default
    private List<T> data = Collections.emptyList();
}
