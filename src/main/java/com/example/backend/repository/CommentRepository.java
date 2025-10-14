
package com.example.backend.repository;

import com.example.backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Đếm số comment của một post
    Long countByPostId(Long postId);
    
    // Lấy tất cả comment của một post
    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);
}