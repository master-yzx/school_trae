package com.campus.modules.user.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 100)
    private String nickname;

    @Column(name = "student_no", length = 32)
    private String studentNo;

    @Column(length = 20)
    private String phone;

    @Column(name = "campus_id")
    private Long campusId;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "register_time", nullable = false)
    private java.time.LocalDateTime registerTime;
}

