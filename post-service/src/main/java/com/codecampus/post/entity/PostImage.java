//package com.codecampus.post.entity;
//
//import com.codecampus.post.entity.audit.AuditMetadata;
//import jakarta.persistence.*;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Entity
//@Table(name = "post_image")
//public class PostImage extends AuditMetadata {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private String postImageId;
//
//    private String imageUrl;
//    private String altText;
//
//    @ManyToOne
//    @JoinColumn(name = "post_id", nullable = false)
//    private Post post;
//}
//
