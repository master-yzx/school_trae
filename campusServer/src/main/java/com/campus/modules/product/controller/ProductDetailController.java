package com.campus.modules.product.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.product.dto.ProductDetailDTO;
import com.campus.modules.product.dto.ProductMediaDTO;
import com.campus.modules.product.dto.RecommendProductDTO;
import com.campus.modules.product.service.ProductDetailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductDetailController {

    private final ProductDetailService productDetailService;

    public ProductDetailController(ProductDetailService productDetailService) {
        this.productDetailService = productDetailService;
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailDTO> detail(@PathVariable("id") Long id) {
        return ApiResponse.success(productDetailService.getDetail(id));
    }

    @GetMapping("/{id}/media")
    public ApiResponse<List<ProductMediaDTO>> media(@PathVariable("id") Long id) {
        return ApiResponse.success(productDetailService.listMedia(id));
    }

    @GetMapping("/{id}/related")
    public ApiResponse<List<RecommendProductDTO>> related(@PathVariable("id") Long id) {
        return ApiResponse.success(productDetailService.listRelated(id));
    }
}

