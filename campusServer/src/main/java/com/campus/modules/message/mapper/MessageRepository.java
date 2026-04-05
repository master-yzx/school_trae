package com.campus.modules.message.mapper;

import com.campus.modules.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

    long countByUserIdAndReadFalse(Long userId);

    Page<Message> findByUserId(Long userId, Pageable pageable);

    void deleteByUserIdAndReadTrue(Long userId);
}

