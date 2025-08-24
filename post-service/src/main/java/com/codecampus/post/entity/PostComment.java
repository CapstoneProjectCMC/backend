package com.codecampus.post.entity;

import com.codecampus.post.entity.audit.AuditMetadata;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
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
@Table(name = "post_comment")
@SQLDelete(sql = "UPDATE post_comment " +
    "SET deleted_by = ? , deleted_at = now() " +
    "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class PostComment extends AuditMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String commentId;

  String userId;
  String content;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  Post post;

  @ManyToOne
  @JoinColumn(name = "parent_comment_id")
  PostComment parentComment; // comment reply

  @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
  List<PostComment> replies;
}

