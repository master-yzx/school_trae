package com.campus.modules.product.service;

import com.campus.common.result.PageResult;
import com.campus.modules.product.dto.ProductPublishRequest;
import com.campus.modules.product.dto.SellerProductDetailDTO;
import com.campus.modules.product.dto.SellerProductListItemDTO;

public interface SellerProductService {

    PageResult<SellerProductListItemDTO> pageSellerProducts(String status, String keyword, long pageNum, long pageSize);

    Long saveDraft(ProductPublishRequest request);

    Long submitForReview(ProductPublishRequest request);

    SellerProductDetailDTO getDetail(Long id);

    void deleteProduct(Long id);

    void changeStatus(Long id, String status);
}

