package com.campus.modules.product.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "campus_id")
    private Long campusId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "cover_url", length = 255)
    private String coverUrl;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "condition_text", length = 50)
    private String conditionText;

    @Column(name = "delivery_type_text", length = 50)
    private String deliveryTypeText;

    @Column(name = "free_shipping", nullable = false)
    private Boolean freeShipping;

    @Column(name = "pickup_location", length = 255)
    private String pickupLocation;

    @Column(name = "description_html", columnDefinition = "TEXT")
    private String descriptionHtml;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Column(name = "favorite_count", nullable = false)
    private Integer favoriteCount;

    @Column(name = "consult_count", nullable = false)
    private Integer consultCount;

    @Column(name = "reject_reason", length = 255)
    private String rejectReason;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;
}

