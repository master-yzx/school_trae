package com.campus.modules.admin.settings.service;

import com.campus.modules.admin.settings.dto.CampusDTO;
import com.campus.modules.admin.settings.dto.SystemConfigDTO;

import java.util.List;

public interface AdminSettingsService {

    SystemConfigDTO getConfig();

    void saveConfig(SystemConfigDTO dto);

    List<CampusDTO> listCampus();

    void saveCampus(CampusDTO dto);

    void deleteCampus(Long id);

    void clearCache();

    void updateSecurityRule(String passwordRule);
}

