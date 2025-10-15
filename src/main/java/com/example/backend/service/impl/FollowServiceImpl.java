package com.example.backend.service.impl;

import com.example.backend.dto.response.FollowResponse;
import com.example.backend.dto.response.FollowStatsResponse;
import com.example.backend.entity.Follower;
import com.example.backend.entity.User;
import com.example.backend.event.NewFollowerEvent;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.DuplicateResourceException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.FollowerRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowServiceImpl implements FollowService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void followUser(Long currentUserId, Long targetUserId) {
        log.info("User {} attempting to follow user {}", currentUserId, targetUserId);

        // Kiểm tra không thể follow chính mình
        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself");
        }

        // Kiểm tra users tồn tại
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        // Kiểm tra đã follow chưa
        if (followerRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId)) {
            throw new DuplicateResourceException("You are already following this user");
        }

        // Tạo follow relationship
        Follower follower = Follower.builder()
                .follower(currentUser)
                .following(targetUser)
                .build();

        followerRepository.save(follower);

        // Publish event để tạo notification
        eventPublisher.publishEvent(new NewFollowerEvent(this, currentUserId, targetUserId));

        log.info("User {} successfully followed user {}", currentUserId, targetUserId);
    }

    @Override
    @Transactional
    public void unfollowUser(Long currentUserId, Long targetUserId) {
        log.info("User {} attempting to unfollow user {}", currentUserId, targetUserId);

        // Kiểm tra không thể unfollow chính mình
        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("Invalid unfollow operation");
        }

        // Kiểm tra follow relationship tồn tại
        if (!followerRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId)) {
            throw new ResourceNotFoundException("You are not following this user");
        }

        // Xóa follow relationship
        followerRepository.deleteByFollowerIdAndFollowingId(currentUserId, targetUserId);

        log.info("User {} successfully unfollowed user {}", currentUserId, targetUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowResponse> getFollowers(Long userId, Long currentUserId, int page, int size) {
        log.info("Getting followers for user {}", userId);

        // Kiểm tra user tồn tại
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> followers = followerRepository.findFollowersByUserId(userId, pageable);

        return followers.map(follower -> mapToFollowResponse(follower, currentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowResponse> getFollowing(Long userId, Long currentUserId, int page, int size) {
        log.info("Getting following for user {}", userId);

        // Kiểm tra user tồn tại
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> following = followerRepository.findFollowingByUserId(userId, pageable);

        return following.map(user -> mapToFollowResponse(user, currentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public FollowStatsResponse getFollowStats(Long userId, Long currentUserId) {
        log.info("Getting follow stats for user {}", userId);

        // Kiểm tra user tồn tại
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        // SỬ DỤNG CODE CŨ ĐÃ CÓ
        Long followersCount = followerRepository.countByFollowingId(userId);
        Long followingCount = followerRepository.countByFollowerId(userId);
        Boolean isFollowing = currentUserId != null && 
                followerRepository.existsByFollowerIdAndFollowingId(currentUserId, userId);

        return FollowStatsResponse.builder()
                .followersCount(followersCount)
                .followingCount(followingCount)
                .isFollowing(isFollowing)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long currentUserId, Long targetUserId) {
        return followerRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId);
    }

    /**
     * Helper method để map User entity sang FollowResponse
     */
    private FollowResponse mapToFollowResponse(User user, Long currentUserId) {
        Boolean isFollowing = currentUserId != null && 
                followerRepository.existsByFollowerIdAndFollowingId(currentUserId, user.getId());
        
        Boolean isFollower = currentUserId != null && 
                followerRepository.existsByFollowerIdAndFollowingId(user.getId(), currentUserId);

        return FollowResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .isFollowing(isFollowing)
                .isFollower(isFollower)
                .build();
    }
}