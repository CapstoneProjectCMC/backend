package com.codecampus.submission.helper;

import com.codecampus.submission.constant.sort.SortField;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class SortHelper {

  public Sort build(SortField field, boolean asc) {
    return asc ? Sort.by(field.getColumn()).ascending()
        : Sort.by(field.getColumn()).descending();
  }
}
