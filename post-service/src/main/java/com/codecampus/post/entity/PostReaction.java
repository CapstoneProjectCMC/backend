package com.codecampus.post.entity;

import com.codecampus.post.entity.audit.AuditMetadata;
import jakarta.persistence.Entity;
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
@Table(name = "post_reaction")
@SQLDelete(sql = "UPDATE post_reaction " +
        "SET deleted_by = ? , deleted_at = now() " +
        "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class PostReaction extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String reactionId;

    String userId;
    String commentId; // for comment reactions
    String emojiType;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    Post post;
}
