package com.codecampus.post.entity;

import com.codecampus.post.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "post_access")
@SQLDelete(sql = "UPDATE post_access " +
    "SET deleted_by = ? , deleted_at = now() " +
    "WHERE post_access_id = ?")
@Where(clause = "deleted_at IS NULL")
public class PostAccess extends AuditMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String postAccessId;

  @JsonBackReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  Post post;

  String userId;
  Boolean isExcluded;
}

