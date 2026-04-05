package com.campus.modules.admin.log.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_log")
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String operator;

    @Column(length = 50)
    private String type;

    @Column(length = 1000)
    private String content;

    @Column(length = 50)
    private String ip;

    @Column(length = 20)
    private String result;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

