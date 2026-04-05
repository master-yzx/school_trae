package com.campus.modules.systemconfig.service.impl;

import com.campus.modules.systemconfig.dto.FooterConfigDTO;
import com.campus.modules.systemconfig.service.SystemConfigService;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Override
    public FooterConfigDTO getFooterConfig() {
        FooterConfigDTO dto = new FooterConfigDTO();
        dto.setCopyright("© " + java.time.Year.now().getValue() + " 校园二手交易平台");
        dto.setNotice("校园内部闲置交易，请注意当面核验物品与安全");
        dto.setServiceContact("campus-support@example.com");
        dto.setRuleLink("#");
        dto.setPrivacyLink("#");
        return dto;
    }
}

