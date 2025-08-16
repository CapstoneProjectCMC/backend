package com.codecampus.post.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "post_access")
public class PostAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String postAccessId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private String userId;
    private Boolean isExcluded;
}

