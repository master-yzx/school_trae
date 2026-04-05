package com.campus.modules.admin.user.service;

import com.campus.common.result.PageResult;
import com.campus.modules.admin.user.dto.AdminUserDTO;

public interface AdminUserService {

    PageResult<AdminUserDTO> pageUsers(String status, String keyword, String campusName,
                                       String startTime, String endTime, long pageNum, long pageSize);

    AdminUserDTO getDetail(Long id);

    void save(AdminUserDTO dto);

    void disable(Long id);

    void enable(Long id);

    void delete(Long id);

    void batchDisable(Long[] ids);

    void batchEnable(Long[] ids);
}

