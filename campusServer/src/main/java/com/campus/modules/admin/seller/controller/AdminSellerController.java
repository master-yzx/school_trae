package com.campus.modules.admin.seller.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.admin.content.dto.AdminProductListDTO;
import com.campus.modules.admin.seller.dto.AdminSellerDTO;
import com.campus.modules.admin.seller.service.AdminSellerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sellers")
public class AdminSellerController {

    private final AdminSellerService adminSellerService;

    public AdminSellerController(AdminSellerService adminSellerService) {
        this.adminSellerService = adminSellerService;
    }

    @GetMapping
    public ApiResponse<PageResult<AdminSellerDTO>> page(@RequestParam(required = false) String status,
                                                        @RequestParam(required = false) String keyword,
                                                        @RequestParam(required = false) String campusName,
                                                        @RequestParam(required = false) String startTime,
                                                        @RequestParam(required = false) String endTime,
                                                        @RequestParam(defaultValue = "1") long pageNum,
                                                        @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(
                adminSellerService.pageSellers(status, keyword, campusName, startTime, endTime, pageNum, pageSize)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminSellerDTO> detail(@PathVariable Long id) {
        return ApiResponse.success(adminSellerService.getDetail(id));
    }

    @PostMapping("/save")
    public ApiResponse<Void> save(@RequestBody AdminSellerDTO dto) {
        adminSellerService.save(dto);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/disable")
    public ApiResponse<Void> disable(@PathVariable Long id) {
        adminSellerService.disable(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/enable")
    public ApiResponse<Void> enable(@PathVariable Long id) {
        adminSellerService.enable(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminSellerService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/batch/disable")
    public ApiResponse<Void> batchDisable(@RequestBody IdsRequest ids) {
        adminSellerService.batchDisable(ids.getIds());
        return ApiResponse.success();
    }

    @PostMapping("/batch/enable")
    public ApiResponse<Void> batchEnable(@RequestBody IdsRequest ids) {
        adminSellerService.batchEnable(ids.getIds());
        return ApiResponse.success();
    }

    @GetMapping("/{id}/products")
    public ApiResponse<List<AdminProductListDTO>> products(@PathVariable Long id) {
        return ApiResponse.success(adminSellerService.listProducts(id));
    }

    public static class IdsRequest {
        private Long[] ids;

        public Long[] getIds() {
            return ids;
        }

        public void setIds(Long[] ids) {
            this.ids = ids;
        }
    }
}

