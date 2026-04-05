package com.campus.modules.admin.review.dto;

import lombok.Data;

@Data
public class AdminReviewProductDTO {
    private Long id;
    private String title;
    private String coverUrl;
    private Integer price;
    private String sellerName;
    private String campusName;
    private String status;
    private String statusText;
    private String submittedAt;
}

