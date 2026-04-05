package com.campus.modules.history.dto;

import lombok.Data;

@Data
public class BrowseRecordDTO {
    private Long id;
    private Long productId;
    private String title;
    private String coverUrl;
    private String campusName;
    private String sellerName;
    private Integer price;
    private String conditionText;
    private String viewedAt;
}

