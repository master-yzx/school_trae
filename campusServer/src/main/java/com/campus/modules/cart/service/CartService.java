package com.campus.modules.cart.service;

import com.campus.common.result.PageResult;
import com.campus.modules.cart.dto.CartItemDTO;

public interface CartService {

    PageResult<CartItemDTO> pageCart(String keyword, long pageNum, long pageSize);

    void addItem(Long productId, Integer quantity);

    void clearCart();

    void deleteItem(Long id);

    void updateQuantity(Long id, Integer quantity);
}

