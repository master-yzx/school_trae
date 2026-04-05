package com.campus.modules.history.mapper;

import com.campus.modules.history.entity.BrowseHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BrowseHistoryRepository extends JpaRepository<BrowseHistory, Long>, JpaSpecificationExecutor<BrowseHistory> {

    Page<BrowseHistory> findByUserId(Long userId, Pageable pageable);

    void deleteByUserId(Long userId);
}

