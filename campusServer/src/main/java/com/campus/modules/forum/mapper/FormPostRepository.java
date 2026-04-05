package com.campus.modules.forum.mapper;

import com.campus.modules.forum.entity.FormPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FormPostRepository extends JpaRepository<FormPost, Long> {

    /** 使用原生 SQL 直接查 forum_post 表，避免 JPQL 映射导致查不到数据 */
    @Query(value = "SELECT * FROM forum_post ORDER BY created_at DESC", nativeQuery = true)
    List<FormPost> findLatest();

    @Query(value = "SELECT * FROM forum_post ORDER BY like_count DESC, view_count DESC", nativeQuery = true)
    List<FormPost> findHot();
}

