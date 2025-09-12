package com.codecampus.payment.entity.audit;

public interface SoftDeletable {
  void markDeleted(String by);

  boolean isDeleted();

  String getDeletedBy();
}
