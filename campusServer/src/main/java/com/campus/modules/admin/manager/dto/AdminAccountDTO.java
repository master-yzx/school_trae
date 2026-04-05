package com.campus.modules.admin.manager.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminAccountDTO {

    private Long id;

    private String username;

    /**
     * 新建或修改管理员时使用的登录密码（前端表单可选字段）。
     * 仅在创建/主动修改时传入，列表查询不会返回该字段。
     */
    private String password;

    private String roleName;

    private String phone;

    private String createdAt;

    private List<String> permissions;
}

