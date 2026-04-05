package com.campus.modules.chat.mapper;

import com.campus.modules.chat.entity.ChatSession;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    ChatSession findFirstByBuyerIdAndSellerIdAndProductId(Long buyerId, Long sellerId, Long productId);

    Page<ChatSession> findByBuyerIdOrSellerIdOrderByLastTimeDesc(Long buyerId, Long sellerId, Pageable pageable);

    @Query("select coalesce(sum(s.unreadBuyer), 0) from ChatSession s where s.buyerId = :userId")
    long sumUnreadBuyerByBuyerId(@Param("userId") Long userId);

    @Query("select coalesce(sum(s.unreadSeller), 0) from ChatSession s where s.sellerId = :userId")
    long sumUnreadSellerBySellerId(@Param("userId") Long userId);
}

