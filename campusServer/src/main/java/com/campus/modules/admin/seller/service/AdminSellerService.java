package com.campus.modules.admin.seller.service;

import com.campus.common.result.PageResult;
import com.campus.modules.admin.content.dto.AdminProductListDTO;
import com.campus.modules.admin.seller.dto.AdminSellerDTO;

import java.util.List;

public interface AdminSellerService {

    PageResult<AdminSellerDTO> pageSellers(String status, String keyword, String campusName,
                                           String startTime, String endTime, long pageNum, long pageSize);

    AdminSellerDTO getDetail(Long id);

    void save(AdminSellerDTO dto);

    void disable(Long id);

    void enable(Long id);

    void delete(Long id);

    void batchDisable(Long[] ids);

    void batchEnable(Long[] ids);

    List<AdminProductListDTO> listProducts(Long sellerId);
}

