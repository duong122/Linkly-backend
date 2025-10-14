package com.example.backend.controller;

import com.example.backend.dto.request.CommentRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.CommentResponse;
import com.example.backend.security.UserPrincipal;
import com.example.backend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        CommentResponse comment = commentService.createComment(postId, request, currentUser.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment created successfully", comment));
    }
    
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentResponse> comments = commentService.getCommentsByPost(postId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }
    
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(@PathVariable Long commentId) {
        CommentResponse comment = commentService.getCommentById(commentId);
        
        return ResponseEntity.ok(ApiResponse.success("Comment retrieved successfully", comment));
    }
    
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        CommentResponse comment = commentService.updateComment(commentId, request, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", comment));
    }
    
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        commentService.deleteComment(commentId, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully"));
    }
    
    @GetMapping("/posts/{postId}/comments/count")
    public ResponseEntity<ApiResponse<Long>> getCommentCount(@PathVariable Long postId) {
        long count = commentService.getCommentCount(postId);
        
        return ResponseEntity.ok(ApiResponse.success("Comment count retrieved successfully", count));
    }
}