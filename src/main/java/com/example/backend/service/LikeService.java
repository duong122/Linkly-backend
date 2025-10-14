
package com.example.backend.service;

public interface LikeService {
    
    void likePost(Long postId, Long userId);
    
    void unlikePost(Long postId, Long userId);
    
    boolean isPostLikedByUser(Long postId, Long userId);
    
    long getLikeCount(Long postId);
}