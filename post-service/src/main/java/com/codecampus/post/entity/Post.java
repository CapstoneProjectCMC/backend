package com.codecampus.post.entity;

import com.codecampus.post.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "post")
@SQLDelete(sql = "UPDATE post " +
        "SET deleted_by = ? , deleted_at = now() " +
        "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Post extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String postId;

    String userId;
    String orgId;
    String postType; //global, organization, group, etc.
    String title;
    String content;
    Boolean isPublic;
    Boolean allowComment;
    String hashtag;
    String status;
    List<String> imagesUrls;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonIgnore
    List<PostComment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<PostReaction> reactions;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<PostAccess> accesses;
}

