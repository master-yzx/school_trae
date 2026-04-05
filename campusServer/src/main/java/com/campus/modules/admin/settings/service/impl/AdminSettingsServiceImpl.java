package com.campus.modules.admin.settings.service.impl;

import com.campus.modules.admin.settings.dto.CampusDTO;
import com.campus.modules.admin.settings.dto.SystemConfigDTO;
import com.campus.modules.admin.settings.entity.SystemConfig;
import com.campus.modules.admin.settings.mapper.SystemConfigRepository;
import com.campus.modules.admin.settings.service.AdminSettingsService;
import com.campus.modules.campus.entity.Campus;
import com.campus.modules.campus.mapper.CampusRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminSettingsServiceImpl implements AdminSettingsService {

    private final CampusRepository campusRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final StringRedisTemplate stringRedisTemplate;

    public AdminSettingsServiceImpl(CampusRepository campusRepository,
                                    SystemConfigRepository systemConfigRepository,
                                    StringRedisTemplate stringRedisTemplate) {
        this.campusRepository = campusRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public SystemConfigDTO getConfig() {
        SystemConfig config = loadOrInitConfig();
        SystemConfigDTO dto = new SystemConfigDTO();
        dto.setPlatformName(config.getPlatformName());
        dto.setHomepageNotice(config.getHomepageNotice());
        dto.setCopyrightText(config.getCopyrightText());
        dto.setServiceContact(config.getServiceContact());
        dto.setTradeRules(config.getTradeRules());
        dto.setViolationRules(config.getViolationRules());
        dto.setLogoUrl(config.getLogoUrl());
        return dto;
    }

    @Override
    public void saveConfig(SystemConfigDTO dto) {
        SystemConfig config = loadOrInitConfig();
        config.setPlatformName(dto.getPlatformName());
        config.setHomepageNotice(dto.getHomepageNotice());
        config.setCopyrightText(dto.getCopyrightText());
        config.setServiceContact(dto.getServiceContact());
        config.setTradeRules(dto.getTradeRules());
        config.setViolationRules(dto.getViolationRules());
        config.setLogoUrl(dto.getLogoUrl());
        systemConfigRepository.save(config);
    }

    @Override
    public List<CampusDTO> listCampus() {
        List<Campus> campuses = campusRepository.findByEnabledTrueOrderByIdAsc();
        return campuses.stream().map(this::toDto).collect(Collectors.toList());
    }

    private CampusDTO toDto(Campus campus) {
        CampusDTO dto = new CampusDTO();
        dto.setId(campus.getId());
        dto.setName(campus.getName());
        dto.setCity(campus.getCity());
        dto.setEnabled(campus.getEnabled());
        return dto;
    }

    @Override
    public void saveCampus(CampusDTO dto) {
        Campus entity = new Campus();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCity(dto.getCity());
        entity.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : Boolean.TRUE);
        campusRepository.save(entity);
    }

    @Override
    public void deleteCampus(Long id) {
        campusRepository.deleteById(id);
    }

    @Override
    public void clearCache() {
        if (stringRedisTemplate.getConnectionFactory() != null) {
            stringRedisTemplate.getConnectionFactory()
                    .getConnection()
                    .serverCommands()
                    .flushDb();
        }
    }

    @Override
    public void updateSecurityRule(String passwordRule) {
        SystemConfig config = loadOrInitConfig();
        config.setPasswordRule(passwordRule);
        systemConfigRepository.save(config);
    }

    private SystemConfig loadOrInitConfig() {
        return systemConfigRepository.findById(1L).orElseGet(() -> {
            SystemConfig c = new SystemConfig();
            c.setId(1L);
            c.setPlatformName("校园二手交易平台");
            c.setHomepageNotice("欢迎使用校园二手交易平台，文明交易，安全第一。");
            c.setCopyrightText("© 2026 校园二手交易平台");
            c.setServiceContact("QQ：123456 / 微信：campus-helper");
            c.setTradeRules("禁止发布违法违规商品，交易前请确认对方身份。");
            c.setViolationRules("发现违规将视情节禁用账号。");
            return systemConfigRepository.save(c);
        });
    }
}

