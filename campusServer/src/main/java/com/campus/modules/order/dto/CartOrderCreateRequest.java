package com.campus.modules.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartOrderCreateRequest {

    private List<Item> items;

    @Data
    public static class Item {
        private Long productId;
        private Integer quantity;
    }
}

