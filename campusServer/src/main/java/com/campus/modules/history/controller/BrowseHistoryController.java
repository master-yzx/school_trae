package com.campus.modules.history.controller;

import com.campus.common.result.ApiResponse;
import com.campus.common.result.PageResult;
import com.campus.modules.history.dto.BrowseRecordDTO;
import com.campus.modules.history.service.BrowseHistoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class BrowseHistoryController {

    private final BrowseHistoryService browseHistoryService;

    public BrowseHistoryController(BrowseHistoryService browseHistoryService) {
        this.browseHistoryService = browseHistoryService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<BrowseRecordDTO>> list(@RequestParam(required = false) String keyword,
                                                         @RequestParam(defaultValue = "1") long pageNum,
                                                         @RequestParam(defaultValue = "12") long pageSize) {
        return ApiResponse.success(browseHistoryService.pageHistory(keyword, pageNum, pageSize));
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clear() {
        browseHistoryService.clearAll();
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        browseHistoryService.deleteOne(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/favorite")
    public ApiResponse<Void> addFavorite(@PathVariable Long id) {
        browseHistoryService.addToFavorite(id);
        return ApiResponse.success();
    }

    @PostMapping("/add")
    public ApiResponse<Void> add(@RequestParam Long productId) {
        browseHistoryService.addRecord(productId);
        return ApiResponse.success();
    }
}

