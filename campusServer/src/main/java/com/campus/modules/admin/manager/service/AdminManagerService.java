package com.campus.modules.admin.manager.service;

import com.campus.common.result.PageResult;
import com.campus.modules.admin.manager.dto.AdminAccountDTO;

public interface AdminManagerService {

    PageResult<AdminAccountDTO> pageList(long pageNum, long pageSize);

    /**
     * 判断系统中是否已存在指定用户名/账号的用户（包括普通用户、卖家、管理员）。
     */
    boolean existsUser(String username);

    void save(AdminAccountDTO dto);

    void delete(Long id);

    void assignPermissions(Long id, java.util.List<String> permissions);

    /**
     * 获取当前登录管理员拥有的权限编码列表。
     */
    java.util.List<String> getCurrentPermissions();
}

