package com.campus.modules.upload.controller;

import com.campus.common.utils.UploadPaths;
import com.campus.common.result.ApiResponse;
import com.campus.modules.upload.dto.UploadResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping
    public ApiResponse<UploadResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ApiResponse.success(null);
        }

        Path uploadRoot = UploadPaths.getUploadDir();
        Files.createDirectories(uploadRoot);

        String original = file.getOriginalFilename();
        String filename = StringUtils.hasText(original) ? original : "file";
        String ext = "";
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            ext = filename.substring(dot);
        }

        String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = uploadRoot.resolve(storedName);
        file.transferTo(target.toFile());

        UploadResponse resp = new UploadResponse();
        resp.setOriginalFilename(original);
        resp.setUrl("/upload/" + storedName);

        return ApiResponse.success(resp);
    }
}

