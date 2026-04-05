package com.campus.modules.admin.manager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "admin_account")
public class AdminAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 角色名称：例如「超级管理员」「普通管理员」
     */
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(length = 20)
    private String phone;

    @Column(name = "permissions", length = 500)
    private String permissions; // 使用逗号分隔的权限编码

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

