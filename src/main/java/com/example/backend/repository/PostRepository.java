package com.example.backend.repository;

import com.example.backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Lấy tất cả bài post của một user
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Lấy feed - bài post của chính user (tạm thời chỉ lấy post của user, chưa include following)
    // Sẽ update sau khi có Follow feature
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    Page<Post> findFeedPosts(@Param("userId") Long userId, Pageable pageable);
    
    // Đếm số post của user
    Long countByUserId(Long userId);
    
    // Tìm bài post theo caption
    @Query("SELECT p FROM Post p WHERE LOWER(p.caption) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.createdAt DESC")
    Page<Post> searchByCaption(@Param("keyword") String keyword, Pageable pageable);
}