package com.codecampus.submission.constant.sort;

import lombok.Getter;

public enum SortField {
  CREATED_AT("createdAt"),
  UPDATED_AT("updatedAt"),
  DELETED_AT("deletedAt"),
  CREATED_BY("createdBy"),
  UPDATED_BY("updatedBy"),
  DELETED_BY("deletedBy"),
  ORDER_IN_QUIZ("orderInQuiz");

  @Getter
  private final String column;

  SortField(String column) {
    this.column = column;
  }
}