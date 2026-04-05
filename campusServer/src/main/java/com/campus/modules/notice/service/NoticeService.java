package com.campus.modules.notice.service;

import com.campus.common.result.PageResult;
import com.campus.modules.notice.dto.NoticeDTO;
import com.campus.modules.notice.dto.NoticeSaveRequest;

public interface NoticeService {

    PageResult<NoticeDTO> pagePublic(String type, String keyword, long pageNum, long pageSize);

    NoticeDTO getPublicDetail(Long id);

    PageResult<NoticeDTO> pageAdmin(String type, String keyword, Boolean enabled, long pageNum, long pageSize);

    NoticeDTO save(NoticeSaveRequest request);

    void delete(Long id);
}

