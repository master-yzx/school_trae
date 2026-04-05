package com.campus.modules.chat.mapper;

import com.campus.modules.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop50BySessionIdOrderByCreatedAtDesc(Long sessionId);
}

