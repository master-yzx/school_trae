package com.campus.modules.favorite.dto;

import lombok.Data;

@Data
public class FavoriteItemDTO {
    private Long id;
    private Long productId;
    private String title;
    private String coverUrl;
    private String campusName;
    private String sellerName;
    private Integer price;
}

