package com.campus.modules.admin.settings.dto;

import lombok.Data;

@Data
public class SystemConfigDTO {

    private String platformName;

    private String homepageNotice;

    private String copyrightText;

    private String serviceContact;

    private String tradeRules;

    private String violationRules;

    private String logoUrl;
}

