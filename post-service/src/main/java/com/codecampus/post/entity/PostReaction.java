package com.codecampus.post.entity;

import com.codecampus.post.entity.audit.AuditMetadata;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "post_reaction")
public class PostReaction extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String reactionId;

    private String userId;
    private String commentId; // for comment reactions
    private String emojiType;


    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
