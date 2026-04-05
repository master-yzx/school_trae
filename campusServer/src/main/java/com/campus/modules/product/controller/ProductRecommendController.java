package com.campus.modules.product.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.product.dto.RecommendProductDTO;
import com.campus.modules.product.service.ProductRecommendService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductRecommendController {

    private final ProductRecommendService productRecommendService;

    public ProductRecommendController(ProductRecommendService productRecommendService) {
        this.productRecommendService = productRecommendService;
    }

    @GetMapping("/recommend")
    public ApiResponse<List<RecommendProductDTO>> recommend() {
        return ApiResponse.success(productRecommendService.listRecommendProducts());
    }
}

