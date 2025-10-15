package com.example.backend.dto.response;

import lombok.*;

/**
 * Response DTO cho follow operations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private Boolean isFollowing; // Current user có đang follow user này không
    private Boolean isFollower;  // User này có đang follow current user không
}