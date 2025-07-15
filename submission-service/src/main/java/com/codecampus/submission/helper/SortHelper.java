package com.codecampus.submission.helper;

import com.codecampus.submission.constant.sort.SortField;
import org.springframework.data.domain.Sort;

public final class SortHelper {

    private SortHelper() {
    }

    public static Sort build(SortField field, boolean asc) {
        return asc ? Sort.by(field.getColumn()).ascending()
                : Sort.by(field.getColumn()).descending();
    }
}
