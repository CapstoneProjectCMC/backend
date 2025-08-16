package com.codecampus.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "post_reaction")
public class PostReaction {
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
