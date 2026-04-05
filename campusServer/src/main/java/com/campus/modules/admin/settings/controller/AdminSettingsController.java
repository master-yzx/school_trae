package com.campus.modules.admin.settings.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.admin.settings.dto.CampusDTO;
import com.campus.modules.admin.settings.dto.SystemConfigDTO;
import com.campus.modules.admin.settings.service.AdminSettingsService;
import com.campus.modules.upload.dto.UploadResponse;
import com.campus.common.utils.UploadPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final AdminSettingsService adminSettingsService;

    @GetMapping
    public ApiResponse<SystemConfigDTO> getConfig() {
        return ApiResponse.success(adminSettingsService.getConfig());
    }

    @PostMapping
    public ApiResponse<Void> saveConfig(@RequestBody SystemConfigDTO dto) {
        adminSettingsService.saveConfig(dto);
        return ApiResponse.success(null);
    }

    @GetMapping("/campus")
    public ApiResponse<List<CampusDTO>> listCampus() {
        return ApiResponse.success(adminSettingsService.listCampus());
    }

    @PostMapping("/campus")
    public ApiResponse<Void> saveCampus(@RequestBody CampusDTO dto) {
        adminSettingsService.saveCampus(dto);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/campus/{id}")
    public ApiResponse<Void> deleteCampus(@PathVariable Long id) {
        adminSettingsService.deleteCampus(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/cache/clear")
    public ApiResponse<Void> clearCache() {
        adminSettingsService.clearCache();
        return ApiResponse.success(null);
    }

    @PostMapping("/security")
    public ApiResponse<Void> updateSecurityRule(@RequestParam String passwordRule) {
        adminSettingsService.updateSecurityRule(passwordRule);
        return ApiResponse.success(null);
    }

    @PostMapping("/logo")
    public ApiResponse<UploadResponse> uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ApiResponse.success(null);
        }

        Path uploadRoot = UploadPaths.getUploadDir();
        Files.createDirectories(uploadRoot);

        String original = file.getOriginalFilename();
        String filename = (original != null && !original.isBlank()) ? original : "logo";
        String ext = "";
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            ext = filename.substring(dot);
        }
        String storedName = "logo-" + UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = uploadRoot.resolve(storedName);
        file.transferTo(target.toFile());

        UploadResponse resp = new UploadResponse();
        resp.setOriginalFilename(original);
        resp.setUrl("/upload/" + storedName);
        return ApiResponse.success(resp);
    }
}

