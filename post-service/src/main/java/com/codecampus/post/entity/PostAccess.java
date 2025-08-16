package com.codecampus.post.entity;

import com.codecampus.post.entity.audit.AuditMetadata;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.springframework.context.annotation.Lazy;

@Entity
@Data
@Table(name = "post_access")
public class PostAccess extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String postAccessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private String userId;
    private Boolean isExcluded;
}

