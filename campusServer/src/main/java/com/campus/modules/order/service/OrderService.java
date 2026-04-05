package com.campus.modules.order.service;

import com.campus.common.result.PageResult;
import com.campus.modules.order.dto.CartOrderCreateRequest;
import com.campus.modules.order.dto.OrderDetailDTO;
import com.campus.modules.order.dto.OrderListItemDTO;
import com.campus.modules.order.dto.OrderLogDTO;

import java.util.List;

public interface OrderService {

    Long createOrder(Long productId, Integer quantity);

    /**
     * 购物车批量结算创建订单。
     * 要求同一买家、同一卖家，多个商品合并为一个订单，按实际价格汇总总金额。
     */
    Long createOrderFromCart(CartOrderCreateRequest request);

    PageResult<OrderListItemDTO> pageUserOrders(String status, String keyword, String startTime, String endTime, long pageNum, long pageSize);

    PageResult<OrderListItemDTO> pageSellerOrders(String status, String keyword, String startTime, String endTime, long pageNum, long pageSize);

    OrderDetailDTO getDetail(Long id, String role);

    List<OrderLogDTO> listLogs(Long id);

    void cancelOrder(Long id, String role);

    void confirmReceive(Long id);

    void applyAfterSale(Long id);

    void shipOrder(Long id, String logisticsNo);

    void payOrder(Long id, String method);
}

