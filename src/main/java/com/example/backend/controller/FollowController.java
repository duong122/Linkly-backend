
package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.FollowResponse;
import com.example.backend.dto.response.FollowStatsResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.security.UserPrincipal;
import com.example.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các thao tác follow/unfollow
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * Follow một user
     * POST /api/users/{userId}/follow
     */
    @PostMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        followService.followUser(currentUser.getId(), userId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Followed successfully")
                        .build()
        );
    }

    /**
     * Unfollow một user
     * DELETE /api/users/{userId}/follow
     */
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        followService.unfollowUser(currentUser.getId(), userId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Unfollowed successfully")
                        .build()
        );
    }

    /**
     * Lấy danh sách followers của một user
     * GET /api/users/{userId}/followers?page=0&size=20
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse> getFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        Page<FollowResponse> followers = followService.getFollowers(userId, currentUserId, page, size);

        PageResponse<FollowResponse> pageResponse = PageResponse.<FollowResponse>builder()
                .content(followers.getContent())
                .pageNumber(followers.getNumber())
                .pageSize(followers.getSize())
                .totalElements(followers.getTotalElements())
                .totalPages(followers.getTotalPages())
                .last(followers.isLast())
                .build();

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Followers retrieved successfully")
                        .data(pageResponse)
                        .build()
        );
    }

    /**
     * Lấy danh sách following của một user
     * GET /api/users/{userId}/following?page=0&size=20
     */
    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse> getFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        Page<FollowResponse> following = followService.getFollowing(userId, currentUserId, page, size);

        PageResponse<FollowResponse> pageResponse = PageResponse.<FollowResponse>builder()
                .content(following.getContent())
                .pageNumber(following.getNumber())
                .pageSize(following.getSize())
                .totalElements(following.getTotalElements())
                .totalPages(following.getTotalPages())
                .last(following.isLast())
                .build();

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Following retrieved successfully")
                        .data(pageResponse)
                        .build()
        );
    }

    /**
     * Lấy thống kê follow của một user
     * GET /api/users/{userId}/follow-stats
     */
    @GetMapping("/{userId}/follow-stats")
    public ResponseEntity<ApiResponse> getFollowStats(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        FollowStatsResponse stats = followService.getFollowStats(userId, currentUserId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Follow stats retrieved successfully")
                        .data(stats)
                        .build()
        );
    }

    /**
     * Kiểm tra follow status
     * GET /api/users/{userId}/is-following
     */
    @GetMapping("/{userId}/is-following")
    public ResponseEntity<ApiResponse> checkFollowStatus(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        boolean isFollowing = followService.isFollowing(currentUser.getId(), userId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Follow status checked")
                        .data(isFollowing)
                        .build()
        );
    }
}