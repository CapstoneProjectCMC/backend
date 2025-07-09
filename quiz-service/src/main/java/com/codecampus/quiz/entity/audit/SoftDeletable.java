package com.codecampus.quiz.entity.audit;

public interface SoftDeletable {
    void markDeleted(String by);

    boolean isDeleted();

    String getDeletedBy();
}
