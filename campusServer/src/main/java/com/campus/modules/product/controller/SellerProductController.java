package com.campus.modules.product.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.product.dto.ProductPublishRequest;
import com.campus.modules.product.dto.SellerProductListItemDTO;
import com.campus.modules.product.service.SellerProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/products")
public class SellerProductController {

    private final SellerProductService sellerProductService;

    public SellerProductController(SellerProductService sellerProductService) {
        this.sellerProductService = sellerProductService;
    }

    @GetMapping
    public ApiResponse<PageResult<SellerProductListItemDTO>> page(@RequestParam(required = false) String status,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(defaultValue = "1") long pageNum,
                                                                  @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(sellerProductService.pageSellerProducts(status, keyword, pageNum, pageSize));
    }

    @PostMapping("/draft")
    public ApiResponse<Long> saveDraft(@Valid @RequestBody ProductPublishRequest request) {
        return ApiResponse.success(sellerProductService.saveDraft(request));
    }

    @PostMapping("/submit")
    public ApiResponse<Long> submit(@Valid @RequestBody ProductPublishRequest request) {
        return ApiResponse.success(sellerProductService.submitForReview(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<com.campus.modules.product.dto.SellerProductDetailDTO> getDetail(@PathVariable Long id) {
        return ApiResponse.success(sellerProductService.getDetail(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sellerProductService.deleteProduct(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/status")
    public ApiResponse<Void> changeStatus(@PathVariable Long id, @RequestParam String status) {
        sellerProductService.changeStatus(id, status);
        return ApiResponse.success();
    }
}

