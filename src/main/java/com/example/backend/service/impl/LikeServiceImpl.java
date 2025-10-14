package com.example.backend.service.impl;

import com.example.backend.entity.Like;
import com.example.backend.entity.LikeId;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.LikeRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    
    @Override
    @Transactional
    public void likePost(Long postId, Long userId) {
        // Check if post exists
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        
        // Check if already liked
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new BadRequestException("You have already liked this post");
        }
        
        // Create and save like
        Like like = new Like();
        like.setId(new LikeId(userId, postId));
        likeRepository.save(like);
    }
    
    @Override
    @Transactional
    public void unlikePost(Long postId, Long userId) {
        // Check if post exists
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        
        // Check if liked
        if (!likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new BadRequestException("You have not liked this post");
        }
        
        // Delete like using composite key
        LikeId likeId = new LikeId(userId, postId);
        likeRepository.deleteById(likeId);
    }
    
    @Override
    public boolean isPostLikedByUser(Long postId, Long userId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }
    
    @Override
    public long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }
}