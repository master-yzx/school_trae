package com.campus.modules.admin.order.service;

import com.campus.common.result.PageResult;
import com.campus.modules.admin.order.dto.AdminOrderDTO;

public interface AdminOrderService {

    PageResult<AdminOrderDTO> pageList(String status, String buyerKeyword, String sellerKeyword,
                                       String orderNo, String startTime, String endTime,
                                       long pageNum, long pageSize);

    AdminOrderDTO getDetail(Long id);

    void cancelOrder(Long id);

    void handleAfterSale(Long id);

    String export(String status, String buyerKeyword, String sellerKeyword,
                  String orderNo, String startTime, String endTime);
}

