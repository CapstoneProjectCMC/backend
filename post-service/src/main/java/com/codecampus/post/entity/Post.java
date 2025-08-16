package com.codecampus.post.entity;

import com.codecampus.post.entity.audit.AuditMetadata;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "post")
public class Post extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String postId;

    private String userId;
    private String orgId;
    private String postType; //global, organization, group, etc.
    private String title;
    private String content;
    private Boolean isPublic;
    private Boolean allowComment;
    private String hashtag;
    private String status;
    private List<String> imagesUrls;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostReaction> reactions;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostAccess> accesses;
}

