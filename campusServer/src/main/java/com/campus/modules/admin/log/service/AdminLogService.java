package com.campus.modules.admin.log.service;

import com.campus.common.result.PageResult;
import com.campus.modules.admin.log.dto.AdminLogDTO;

public interface AdminLogService {

    PageResult<AdminLogDTO> pageList(String operator, String type, String result,
                                     String keyword, String startTime, String endTime,
                                     long pageNum, long pageSize);

    AdminLogDTO detail(Long id);

    /**
     * 导出日志，返回生成文件的可访问 URL（例如 /upload/xxx.csv）
     */
    String export(String operator, String type, String result,
                  String keyword, String startTime, String endTime);
}

