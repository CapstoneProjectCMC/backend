package com.codecampus.payment.entity.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AuditMetadata implements SoftDeletable {
  /* ---- create ---- */
  @CreatedBy
  @Column(name = "created_by")
  String createdBy;

  @CreatedDate
  @Column(name = "created_at")
  Instant createdAt;

  /* ---- update ---- */
  @LastModifiedBy
  @Column(name = "updated_by")
  String updatedBy;

  @LastModifiedDate
  @Column(name = "updated_at")
  Instant updatedAt;

  /* ---- soft-delete ---- */
  @Column(name = "deleted_by")
  String deletedBy;

  @Column(name = "deleted_at")
  Instant deletedAt;

  @Override
  public void markDeleted(String by) {
    this.deletedBy = by;
    this.deletedAt = Instant.now();
  }

  @Override
  public boolean isDeleted() {
    return deletedAt != null;
  }
}
