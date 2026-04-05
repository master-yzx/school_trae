package com.campus.modules.banner.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 255)
    private String subtitle;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(nullable = false)
    private Boolean enabled;
}

