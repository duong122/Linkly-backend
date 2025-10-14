
package com.example.backend.service;

import com.example.backend.dto.request.CommentRequest;
import com.example.backend.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    
    CommentResponse createComment(Long postId, CommentRequest request, Long userId);
    
    CommentResponse updateComment(Long commentId, CommentRequest request, Long userId);
    
    void deleteComment(Long commentId, Long userId);
    
    Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable);
    
    CommentResponse getCommentById(Long commentId);
    
    long getCommentCount(Long postId);
}