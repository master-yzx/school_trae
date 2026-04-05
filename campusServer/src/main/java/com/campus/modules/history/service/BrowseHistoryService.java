package com.campus.modules.history.service;

import com.campus.common.result.PageResult;
import com.campus.modules.history.dto.BrowseRecordDTO;

public interface BrowseHistoryService {

    PageResult<BrowseRecordDTO> pageHistory(String keyword, long pageNum, long pageSize);

    void clearAll();

    void deleteOne(Long id);

    void addToFavorite(Long id);

    /**
     * 记录一次浏览行为。
     */
    void addRecord(Long productId);
}

