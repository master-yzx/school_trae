package com.campus.modules.forum.controller;

import com.campus.common.result.ApiResponse;
import com.campus.modules.forum.dto.FormCommentDTO;
import com.campus.modules.forum.dto.FormPostDetailDTO;
import com.campus.modules.forum.dto.FormPostListDTO;
import com.campus.modules.forum.service.FormService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    @GetMapping("/posts")
    public ApiResponse<List<FormPostListDTO>> list(@RequestParam(defaultValue = "latest") String sort) {
        return ApiResponse.success(formService.listPosts(sort));
    }

    @GetMapping("/posts/{id}")
    public ApiResponse<FormPostDetailDTO> detail(@PathVariable Long id) {
        return ApiResponse.success(formService.getPost(id));
    }

    @PostMapping("/posts")
    public ApiResponse<Void> create(@RequestBody CreatePostRequest request) {
        formService.createPost(request.getTitle(), request.getContent(), request.getProductId());
        return ApiResponse.success();
    }

    @GetMapping("/posts/{id}/comments")
    public ApiResponse<List<FormCommentDTO>> listComments(@PathVariable Long id) {
        return ApiResponse.success(formService.listComments(id));
    }

    @PostMapping("/posts/{id}/comments")
    public ApiResponse<Void> addComment(@PathVariable Long id, @RequestBody CommentRequest request) {
        formService.addComment(id, request.getContent());
        return ApiResponse.success();
    }

    @PostMapping("/posts/{id}/like")
    public ApiResponse<Void> like(@PathVariable Long id) {
        formService.likePost(id);
        return ApiResponse.success();
    }

    public static class CreatePostRequest {
        private String title;
        private String content;
        private Long productId;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }
    }

    public static class CommentRequest {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

