package com.campus.modules.order.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.order.dto.CartOrderCreateRequest;
import com.campus.modules.order.dto.OrderDetailDTO;
import com.campus.modules.order.dto.OrderListItemDTO;
import com.campus.modules.order.dto.OrderLogDTO;
import com.campus.modules.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ApiResponse<Long> create(@RequestParam Long productId, @RequestParam(defaultValue = "1") Integer quantity) {
        Long orderId = orderService.createOrder(productId, quantity);
        if (orderId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "创建订单失败");
        return ApiResponse.success(orderId);
    }

    @PostMapping("/create-from-cart")
    public ApiResponse<Long> createFromCart(@RequestBody CartOrderCreateRequest request) {
        Long orderId = orderService.createOrderFromCart(request);
        if (orderId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "结算失败");
        return ApiResponse.success(orderId);
    }

    @GetMapping("/user")
    public ApiResponse<PageResult<OrderListItemDTO>> pageUserOrders(@RequestParam(required = false) String status,
                                                                    @RequestParam(required = false) String keyword,
                                                                    @RequestParam(required = false) String startTime,
                                                                    @RequestParam(required = false) String endTime,
                                                                    @RequestParam(defaultValue = "1") long pageNum,
                                                                    @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(orderService.pageUserOrders(status, keyword, startTime, endTime, pageNum, pageSize));
    }

    @GetMapping("/seller")
    public ApiResponse<PageResult<OrderListItemDTO>> pageSellerOrders(@RequestParam(required = false) String status,
                                                                      @RequestParam(required = false) String keyword,
                                                                      @RequestParam(required = false) String startTime,
                                                                      @RequestParam(required = false) String endTime,
                                                                      @RequestParam(defaultValue = "1") long pageNum,
                                                                      @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(orderService.pageSellerOrders(status, keyword, startTime, endTime, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailDTO> detail(@PathVariable Long id, @RequestParam String role) {
        return ApiResponse.success(orderService.getDetail(id, role));
    }

    @GetMapping("/{id}/logs")
    public ApiResponse<List<OrderLogDTO>> logs(@PathVariable Long id) {
        return ApiResponse.success(orderService.listLogs(id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id, @RequestParam String role) {
        orderService.cancelOrder(id, role);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/confirm")
    public ApiResponse<Void> confirm(@PathVariable Long id) {
        orderService.confirmReceive(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/after-sale")
    public ApiResponse<Void> afterSale(@PathVariable Long id) {
        orderService.applyAfterSale(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/ship")
    public ApiResponse<Void> ship(@PathVariable Long id, @RequestParam String logisticsNo) {
        orderService.shipOrder(id, logisticsNo);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<Void> pay(@PathVariable Long id, @RequestParam String method) {
        orderService.payOrder(id, method);
        return ApiResponse.success();
    }
}

