package com.codecampus.chat.helper;

import com.codecampus.chat.dto.common.PageResponse;
import org.springframework.data.domain.Page;

public class PageResponseHelper {
    private PageResponseHelper() {
    }

    /**
     * Chuyển một {@link Page}&lt;T&gt; thành {@link PageResponse}&lt;T&gt;.
     *
     * @param pageData    đối tượng Spring Data Page chứa dữ liệu và thông tin phân trang
     * @param currentPage số trang hiện tại (bắt đầu từ 1)
     * @param <T>         kiểu của phần tử trong trang
     * @return {@link PageResponse}&lt;T&gt; tương ứng
     */
    public static <T> PageResponse<T> toPageResponse(
            Page<T> pageData, int currentPage) {
        return PageResponse.<T>builder()
                .currentPage(currentPage)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().toList())
                .build();
    }
}
