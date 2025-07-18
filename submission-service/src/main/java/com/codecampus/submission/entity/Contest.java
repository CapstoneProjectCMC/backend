package com.codecampus.submission.entity;

import com.codecampus.submission.entity.audit.AuditMetadata;
import com.codecampus.submission.entity.data.AntiCheatConfig;
import com.codecampus.submission.entity.data.AntiCheatConfigConverter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "contest")
@SQLDelete(sql = "UPDATE contest " +
        "SET deleted_by = ? , deleted_at = now() " +
        "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Contest extends AuditMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, length = 100)
    String title;

    @Column(columnDefinition = "text")
    String description;

    @Column(name = "org_id")
    String orgId;

    @Column(name = "start_time", nullable = false)
    Instant startTime;

    @Column(name = "end_time", nullable = false)
    Instant endTime;

    @Column(name = "is_rank_public", nullable = false)
    boolean rankPublic;

    Instant rankRevealTime;

    @Convert(converter = AntiCheatConfigConverter.class)
    @Column(name = "anti_cheat_config", columnDefinition = "jsonb")
    AntiCheatConfig antiCheatConfig;

    // Many-to-many: contest – exercise thông qua bảng nối
    @JsonManagedReference
    @ManyToMany
    @JoinTable(
            name = "contest_exercise",
            joinColumns = @JoinColumn(name = "contest_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    Set<Exercise> exercises;
}
