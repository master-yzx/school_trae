package com.campus.modules.campus.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "campus")
public class Campus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String city;

    @Column(nullable = false)
    private Boolean enabled;
}

