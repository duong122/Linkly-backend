package com.example.backend.repository;

import com.example.backend.entity.Follower;
import com.example.backend.entity.FollowerId;
import com.example.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {

    // ========== CODE CŨ - GIỮ NGUYÊN ==========
    Long countByFollowingId(Long followingId);
    
    Long countByFollowerId(Long followerId);
    
    Boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    // ========== CODE MỚI - MỞ RỘNG ==========
    
    /**
     * Lấy danh sách followers của một user
     */
    @Query("SELECT f.follower FROM Follower f WHERE f.following.id = :userId ORDER BY f.createdAt DESC")
    Page<User> findFollowersByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Lấy danh sách following của một user
     */
    @Query("SELECT f.following FROM Follower f WHERE f.follower.id = :userId ORDER BY f.createdAt DESC")
    Page<User> findFollowingByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Lấy danh sách following IDs của một user (để dùng cho feed)
     */
    @Query("SELECT f.following.id FROM Follower f WHERE f.follower.id = :userId")
    List<Long> findFollowingIdsByUserId(@Param("userId") Long userId);

    /**
     * Xóa follow relationship
     */
    @Modifying
    @Query("DELETE FROM Follower f WHERE f.follower.id = :followerId AND f.following.id = :followingId")
    void deleteByFollowerIdAndFollowingId(
            @Param("followerId") Long followerId,
            @Param("followingId") Long followingId
    );
}