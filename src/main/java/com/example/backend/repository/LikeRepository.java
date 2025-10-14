
package com.example.backend.repository;

import com.example.backend.entity.Like;
import com.example.backend.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {
    
    // Đếm số lượng like của một post
    Long countByPostId(Long postId);
    
    // Kiểm tra user đã like post chưa
    Boolean existsByUserIdAndPostId(Long userId, Long postId);
    
    // Xóa like (dùng cho unlike)
    void deleteByUserIdAndPostId(Long userId, Long postId);
}