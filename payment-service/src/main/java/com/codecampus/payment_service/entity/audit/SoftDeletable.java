package com.codecampus.payment_service.entity.audit;

public interface SoftDeletable {
  void markDeleted(String by);

  boolean isDeleted();

  String getDeletedBy();
}
