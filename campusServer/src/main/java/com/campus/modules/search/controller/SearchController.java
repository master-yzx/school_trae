package com.campus.modules.search.controller;

import com.campus.common.result.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @GetMapping("/hot")
    public ApiResponse<List<String>> hotKeywords() {
        List<String> list = Arrays.asList("考研教材", "四六级资料", "闲置手机", "打印机", "耳机");
        return ApiResponse.success(list);
    }
}

