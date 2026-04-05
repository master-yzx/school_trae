package com.campus.modules.admin.content.service;

import com.campus.common.result.PageResult;
import com.campus.modules.admin.content.dto.AdminProductListDTO;

public interface AdminContentService {

    PageResult<AdminProductListDTO> pageProducts(String status, String sellerKeyword, String titleKeyword, Long categoryId,
                                                 long pageNum, long pageSize);

    void audit(Long id, boolean pass, String reason);

    void offline(Long id);

    void delete(Long id);

    void batchOffline(Long[] ids);

    void batchDelete(Long[] ids);

    String getRejectReason(Long id);
}

