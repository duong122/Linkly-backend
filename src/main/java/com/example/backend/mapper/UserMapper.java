
package com.example.backend.mapper;

import com.example.backend.dto.response.UserResponse;
import com.example.backend.entity.User;
import com.example.backend.repository.FollowerRepository;
import com.example.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    
    private final FollowerRepository followerRepository;
    private final PostRepository postRepository;
    
    public UserResponse toUserResponse(User user, Long currentUserId) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setBio(user.getBio());
        response.setCreatedAt(user.getCreatedAt());
        
        response.setFollowersCount(followerRepository.countByFollowingId(user.getId()));
        response.setFollowingCount(followerRepository.countByFollowerId(user.getId()));
        response.setPostsCount(postRepository.countByUserId(user.getId()));
        
        if (currentUserId != null && !currentUserId.equals(user.getId())) {
            response.setIsFollowing(followerRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId()));
        } else {
            response.setIsFollowing(false);
        }
        
        return response;
    }
    
    public UserResponse toUserResponse(User user) {
        return toUserResponse(user, null);
    }
}