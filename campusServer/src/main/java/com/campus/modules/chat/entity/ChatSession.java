package com.campus.modules.chat.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_session")
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "last_message", length = 500)
    private String lastMessage;

    @Column(name = "last_time")
    private LocalDateTime lastTime;

    @Column(name = "unread_buyer", nullable = false)
    private Integer unreadBuyer;

    @Column(name = "unread_seller", nullable = false)
    private Integer unreadSeller;
}

