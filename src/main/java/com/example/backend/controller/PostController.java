package com.example.backend.controller;

import com.example.backend.dto.request.PostCreateRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.PostResponse;
import com.example.backend.security.UserPrincipal;
import com.example.backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    /**
     * Tạo bài post mới
     * POST /api/posts
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "caption", required = false) String caption,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        PostCreateRequest request = new PostCreateRequest(caption);
        PostResponse post = postService.createPost(request, image, currentUser.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo bài post thành công", post));
    }
    
    /**
     * Lấy chi tiết một bài post
     * GET /api/posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        PostResponse post = postService.getPostById(postId, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin post thành công", post));
    }
    
    /**
     * Lấy tất cả bài post của một user
     * GET /api/posts/user/{userId}?page=0&size=10
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponse> posts = postService.getUserPosts(userId, currentUserId, pageable);
        
        PageResponse<PostResponse> pageResponse = PageResponse.<PostResponse>builder()
                .content(posts.getContent())
                .pageNumber(posts.getNumber())
                .pageSize(posts.getSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .last(posts.isLast())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách post thành công", pageResponse));
    }
    
    /**
     * Lấy feed (bài post của user và những người user follow)
     * GET /api/posts/feed?page=0&size=10
     */
    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getFeedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponse> posts = postService.getFeedPosts(currentUser.getId(), pageable);
        
        PageResponse<PostResponse> pageResponse = PageResponse.<PostResponse>builder()
                .content(posts.getContent())
                .pageNumber(posts.getNumber())
                .pageSize(posts.getSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .last(posts.isLast())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Lấy feed thành công", pageResponse));
    }
    
    /**
     * Xóa bài post
     * DELETE /api/posts/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        postService.deletePost(postId, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success("Xóa bài post thành công", null));
    }
    
    /**
     * Cập nhật caption
     * PUT /api/posts/{postId}/caption
     */
    @PutMapping("/{postId}/caption")
    public ResponseEntity<ApiResponse<PostResponse>> updateCaption(
            @PathVariable Long postId,
            @RequestParam String caption,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        PostResponse post = postService.updatePostCaption(postId, caption, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success("Cập nhật caption thành công", post));
    }
    
    /**
     * Tìm kiếm post theo caption
     * GET /api/posts/search?keyword=summer&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponse> posts = postService.searchPosts(keyword, currentUserId, pageable);
        
        PageResponse<PostResponse> pageResponse = PageResponse.<PostResponse>builder()
                .content(posts.getContent())
                .pageNumber(posts.getNumber())
                .pageSize(posts.getSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .last(posts.isLast())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Tìm kiếm thành công", pageResponse));
    }
}