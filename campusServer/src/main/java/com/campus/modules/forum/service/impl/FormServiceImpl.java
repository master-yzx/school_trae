package com.campus.modules.forum.service.impl;

import com.campus.common.auth.CurrentUserHolder;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.ResultCode;
import com.campus.modules.forum.dto.FormCommentDTO;
import com.campus.modules.forum.dto.FormPostDetailDTO;
import com.campus.modules.forum.dto.FormPostListDTO;
import com.campus.modules.forum.entity.FormComment;
import com.campus.modules.forum.entity.FormPost;
import com.campus.modules.forum.mapper.FormCommentRepository;
import com.campus.modules.forum.mapper.FormPostRepository;
import com.campus.modules.forum.service.FormService;
import com.campus.modules.user.entity.UserAccount;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FormServiceImpl implements FormService {

    private final FormPostRepository postRepository;
    private final FormCommentRepository commentRepository;
    private final UserAccountRepository userAccountRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FormServiceImpl(FormPostRepository postRepository,
                           FormCommentRepository commentRepository,
                           UserAccountRepository userAccountRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public List<FormPostListDTO> listPosts(String sort) {
        List<FormPost> list;
        if ("hot".equalsIgnoreCase(sort)) {
            list = postRepository.findHot();
        } else {
            list = postRepository.findLatest();
        }
        List<FormPostListDTO> result = new ArrayList<>();
        for (FormPost p : list) {
            FormPostListDTO dto = new FormPostListDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            String c = p.getContent() == null ? "" : p.getContent();
            dto.setSummary(c.length() > 80 ? c.substring(0, 80) + "..." : c);
            dto.setCreatedAt(p.getCreatedAt() == null ? "" : p.getCreatedAt().format(formatter));
            dto.setViewCount(p.getViewCount());
            dto.setLikeCount(p.getLikeCount());
            dto.setReplyCount(p.getReplyCount());
            dto.setProductId(p.getProductId());
            if (p.getUserId() != null) {
                UserAccount u = userAccountRepository.findById(p.getUserId()).orElse(null);
                if (u != null) {
                    dto.setAuthorNickname(u.getNickname() != null && !u.getNickname().isBlank()
                            ? u.getNickname() : u.getUsername());
                }
            }
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional
    public FormPostDetailDTO getPost(Long id) {
        FormPost p = postRepository.findById(id).orElse(null);
        if (p == null) return null;
        p.setViewCount(p.getViewCount() == null ? 1L : p.getViewCount() + 1);
        postRepository.save(p);

        FormPostDetailDTO dto = new FormPostDetailDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setContent(p.getContent());
        dto.setCreatedAt(p.getCreatedAt() == null ? "" : p.getCreatedAt().format(formatter));
        dto.setViewCount(p.getViewCount());
        dto.setLikeCount(p.getLikeCount());
        dto.setReplyCount(p.getReplyCount());
        dto.setProductId(p.getProductId());
        if (p.getUserId() != null) {
            UserAccount u = userAccountRepository.findById(p.getUserId()).orElse(null);
            if (u != null) {
                dto.setAuthorNickname(u.getNickname() != null && !u.getNickname().isBlank()
                        ? u.getNickname() : u.getUsername());
            }
        }
        return dto;
    }

    @Override
    @Transactional
    public void createPost(String title, String content, Long productId) {
        var current = CurrentUserHolder.get();
        if (current == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        if (title == null || title.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "标题不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "内容不能为空");
        }
        FormPost p = new FormPost();
        p.setUserId(current.getId());
        p.setTitle(title.trim());
        p.setContent(content.trim());
        p.setProductId(productId);
        p.setViewCount(0L);
        p.setLikeCount(0L);
        p.setReplyCount(0L);
        p.setCreatedAt(LocalDateTime.now());
        postRepository.save(p);
    }

    @Override
    public List<FormCommentDTO> listComments(Long postId) {
        List<FormComment> list = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        List<FormCommentDTO> result = new ArrayList<>();
        for (FormComment c : list) {
            FormCommentDTO dto = new FormCommentDTO();
            dto.setId(c.getId());
            dto.setContent(c.getContent());
            dto.setCreatedAt(c.getCreatedAt() == null ? "" : c.getCreatedAt().format(formatter));
            if (c.getUserId() != null) {
                UserAccount u = userAccountRepository.findById(c.getUserId()).orElse(null);
                if (u != null) {
                    dto.setAuthorNickname(u.getNickname() != null && !u.getNickname().isBlank()
                            ? u.getNickname() : u.getUsername());
                }
            }
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional
    public void addComment(Long postId, String content) {
        var current = CurrentUserHolder.get();
        if (current == null) {
            throw new RuntimeException("请先登录");
        }
        FormPost post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        FormComment c = new FormComment();
        c.setPostId(postId);
        c.setUserId(current.getId());
        c.setContent(content);
        c.setCreatedAt(LocalDateTime.now());
        commentRepository.save(c);

        post.setReplyCount(post.getReplyCount() == null ? 1L : post.getReplyCount() + 1);
        post.setLastReplyAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void likePost(Long id) {
        var current = CurrentUserHolder.get();
        if (current == null) {
            throw new RuntimeException("请先登录");
        }
        FormPost p = postRepository.findById(id).orElse(null);
        if (p == null) {
            throw new RuntimeException("帖子不存在");
        }
        p.setLikeCount(p.getLikeCount() == null ? 1L : p.getLikeCount() + 1);
        postRepository.save(p);
    }
}

