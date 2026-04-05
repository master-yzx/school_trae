package com.campus.modules.admin.order.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.admin.order.dto.AdminOrderDTO;
import com.campus.modules.admin.order.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public ApiResponse<PageResult<AdminOrderDTO>> pageList(@RequestParam(required = false) String status,
                                                           @RequestParam(required = false) String buyerKeyword,
                                                           @RequestParam(required = false) String sellerKeyword,
                                                           @RequestParam(required = false) String orderNo,
                                                           @RequestParam(required = false) String startTime,
                                                           @RequestParam(required = false) String endTime,
                                                           @RequestParam(defaultValue = "1") long pageNum,
                                                           @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(
                adminOrderService.pageList(status, buyerKeyword, sellerKeyword, orderNo, startTime, endTime, pageNum, pageSize)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminOrderDTO> detail(@PathVariable Long id) {
        return ApiResponse.success(adminOrderService.getDetail(id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        adminOrderService.cancelOrder(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/after-sale")
    public ApiResponse<Void> handleAfterSale(@PathVariable Long id) {
        adminOrderService.handleAfterSale(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/export")
    public ApiResponse<String> export(@RequestParam(required = false) String status,
                                      @RequestParam(required = false) String buyerKeyword,
                                      @RequestParam(required = false) String sellerKeyword,
                                      @RequestParam(required = false) String orderNo,
                                      @RequestParam(required = false) String startTime,
                                      @RequestParam(required = false) String endTime) {
        String url = adminOrderService.export(status, buyerKeyword, sellerKeyword, orderNo, startTime, endTime);
        return ApiResponse.success(url);
    }
}

