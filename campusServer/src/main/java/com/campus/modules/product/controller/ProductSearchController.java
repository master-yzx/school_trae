package com.campus.modules.product.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.product.dto.ProductListItemDTO;
import com.campus.modules.product.dto.ProductSearchQuery;
import com.campus.modules.product.service.ProductSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    public ProductSearchController(ProductSearchService productSearchService) {
        this.productSearchService = productSearchService;
    }

    @GetMapping("/search")
    public ApiResponse<PageResult<ProductListItemDTO>> search(ProductSearchQuery query) {
        return ApiResponse.success(productSearchService.search(query));
    }
}

