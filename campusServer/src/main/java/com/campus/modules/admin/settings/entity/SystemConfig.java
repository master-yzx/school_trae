package com.campus.modules.admin.settings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "system_config")
public class SystemConfig {

    @Id
    private Long id;

    @Column(name = "platform_name", length = 100)
    private String platformName;

    @Column(name = "homepage_notice", length = 500)
    private String homepageNotice;

    @Column(name = "copyright_text", length = 255)
    private String copyrightText;

    @Column(name = "service_contact", length = 255)
    private String serviceContact;

    @Column(name = "trade_rules", length = 1000)
    private String tradeRules;

    @Column(name = "violation_rules", length = 1000)
    private String violationRules;

    @Column(name = "password_rule", length = 255)
    private String passwordRule;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;
}

