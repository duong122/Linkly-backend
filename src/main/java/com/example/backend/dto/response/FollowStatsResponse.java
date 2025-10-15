package com.example.backend.dto.response;

import lombok.*;

/**
 * Response DTO cho follow statistics
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowStatsResponse {
    private Long followersCount;
    private Long followingCount;
    private Boolean isFollowing; // Current user có đang follow user này không
}