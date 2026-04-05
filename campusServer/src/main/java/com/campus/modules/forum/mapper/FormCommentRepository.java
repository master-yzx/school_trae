package com.campus.modules.forum.mapper;

import com.campus.modules.forum.entity.FormComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormCommentRepository extends JpaRepository<FormComment, Long> {

    List<FormComment> findByPostIdOrderByCreatedAtAsc(Long postId);
}

