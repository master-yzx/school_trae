package com.campus.modules.admin.review.service;

import com.campus.common.result.PageResult;
import com.campus.modules.admin.review.dto.AdminReviewProductDTO;

public interface AdminReviewService {

    PageResult<AdminReviewProductDTO> pageReviewProducts(String status, String sellerKeyword, String titleKeyword, long pageNum, long pageSize);

    void approve(Long id);

    void reject(Long id, String reason);
}

