package com.example.backend.service;

import com.example.backend.dto.response.FollowResponse;
import com.example.backend.dto.response.FollowStatsResponse;
import org.springframework.data.domain.Page;

/**
 * Service interface cho Follow operations
 */
public interface FollowService {

    /**
     * Follow một user
     * @param currentUserId ID của user hiện tại
     * @param targetUserId ID của user cần follow
     */
    void followUser(Long currentUserId, Long targetUserId);

    /**
     * Unfollow một user
     * @param currentUserId ID của user hiện tại
     * @param targetUserId ID của user cần unfollow
     */
    void unfollowUser(Long currentUserId, Long targetUserId);

    /**
     * Lấy danh sách followers của một user
     * @param userId ID của user
     * @param currentUserId ID của user hiện tại (để check follow status)
     * @param page số trang
     * @param size kích thước trang
     * @return Page of FollowResponse
     */
    Page<FollowResponse> getFollowers(Long userId, Long currentUserId, int page, int size);

    /**
     * Lấy danh sách following của một user
     * @param userId ID của user
     * @param currentUserId ID của user hiện tại (để check follow status)
     * @param page số trang
     * @param size kích thước trang
     * @return Page of FollowResponse
     */
    Page<FollowResponse> getFollowing(Long userId, Long currentUserId, int page, int size);

    /**
     * Lấy thống kê follow của một user
     * @param userId ID của user
     * @param currentUserId ID của user hiện tại
     * @return FollowStatsResponse
     */
    FollowStatsResponse getFollowStats(Long userId, Long currentUserId);

    /**
     * Kiểm tra xem current user có đang follow target user không
     * @param currentUserId ID của user hiện tại
     * @param targetUserId ID của target user
     * @return true nếu đang follow, false nếu không
     */
    boolean isFollowing(Long currentUserId, Long targetUserId);
}