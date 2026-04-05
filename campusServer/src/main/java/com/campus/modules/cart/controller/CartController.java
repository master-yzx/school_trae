package com.campus.modules.cart.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.cart.dto.CartItemDTO;
import com.campus.modules.cart.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ApiResponse<Void> add(@RequestParam Long productId, @RequestParam(defaultValue = "1") Integer quantity) {
        cartService.addItem(productId, quantity);
        return ApiResponse.success();
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<CartItemDTO>> list(@RequestParam(required = false) String keyword,
                                                     @RequestParam(defaultValue = "1") long pageNum,
                                                     @RequestParam(defaultValue = "12") long pageSize) {
        return ApiResponse.success(cartService.pageCart(keyword, pageNum, pageSize));
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clear() {
        cartService.clearCart();
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        cartService.deleteItem(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/quantity")
    public ApiResponse<Void> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        cartService.updateQuantity(id, quantity);
        return ApiResponse.success();
    }
}

