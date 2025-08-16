package com.codecampus.post.entity;

import com.codecampus.post.entity.audit.AuditMetadata;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "post_comment")
public class PostComment extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String commentId;

    private String userId;
    private String content;


    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private PostComment parentComment; // comment reply

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<PostComment> replies;
}

