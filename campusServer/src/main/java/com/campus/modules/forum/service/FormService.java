package com.campus.modules.forum.service;

import com.campus.modules.forum.dto.FormCommentDTO;
import com.campus.modules.forum.dto.FormPostDetailDTO;
import com.campus.modules.forum.dto.FormPostListDTO;

import java.util.List;

public interface FormService {

    List<FormPostListDTO> listPosts(String sort);

    FormPostDetailDTO getPost(Long id);

    void createPost(String title, String content, Long productId);

    List<FormCommentDTO> listComments(Long postId);

    void addComment(Long postId, String content);

    void likePost(Long id);
}

