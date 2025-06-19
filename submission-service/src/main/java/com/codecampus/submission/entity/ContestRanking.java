package com.codecampus.submission.entity;

import com.codecampus.submission.entity.audit.AuditMetadata;
import com.codecampus.submission.entity.data.ContestRankId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name = "contest_ranking")
@SQLDelete(sql = "UPDATE contest_ranking SET deleted_at = now(), deleted_by = ? WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ContestRanking extends AuditMetadata {
  @EmbeddedId
  ContestRankId id;

  @MapsId("contestId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contest_id")
  Contest contest;

  @Column(nullable = false, columnDefinition = "smallint")
  Integer score;

  @Column(nullable = false, columnDefinition = "smallint")
  Integer rank;
}
