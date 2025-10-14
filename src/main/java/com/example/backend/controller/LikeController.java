package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.security.UserPrincipal;
import com.example.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {
    
    private final LikeService likeService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        likeService.likePost(postId, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success("Post liked successfully"));
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        likeService.unlikePost(postId, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success("Post unliked successfully"));
    }
    
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLikeStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        boolean isLiked = likeService.isPostLikedByUser(postId, currentUser.getId());
        long likeCount = likeService.getLikeCount(postId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("isLiked", isLiked);
        data.put("likeCount", likeCount);
        
        return ResponseEntity.ok(ApiResponse.success("Like status retrieved successfully", data));
    }
    
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(@PathVariable Long postId) {
        long count = likeService.getLikeCount(postId);
        
        return ResponseEntity.ok(ApiResponse.success("Like count retrieved successfully", count));
    }
}