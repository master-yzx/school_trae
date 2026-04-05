package com.campus.modules.campus.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.campus.dto.CampusDTO;
import com.campus.modules.campus.service.CampusService;
import com.campus.modules.product.dto.RecommendProductDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/campuses")
public class CampusController {

    private final CampusService campusService;

    public CampusController(CampusService campusService) {
        this.campusService = campusService;
    }

    @GetMapping
    public ApiResponse<List<CampusDTO>> list() {
        return ApiResponse.success(campusService.listAll());
    }

    @GetMapping("/{campusId}/products")
    public ApiResponse<List<RecommendProductDTO>> listHotProducts(@PathVariable Long campusId) {
        return ApiResponse.success(campusService.listHotProductsByCampus(campusId));
    }
}

