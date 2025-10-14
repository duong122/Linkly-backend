
package com.example.backend.service.impl;

import com.example.backend.dto.request.CommentRequest;
import com.example.backend.dto.response.CommentResponse;
import com.example.backend.entity.Comment;
import com.example.backend.entity.User;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, Long userId) {
        // Check if post exists
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        
        // Create comment
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        
        Comment savedComment = commentRepository.save(comment);
        
        return mapToResponse(savedComment);
    }
    
    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        // Check if user owns the comment
        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to update this comment");
        }
        
        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        
        return mapToResponse(updatedComment);
    }
    
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        // Check if user owns the comment
        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this comment");
        }
        
        commentRepository.delete(comment);
    }
    
    @Override
    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable) {
        // Check if post exists
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        
        Page<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);
        return comments.map(this::mapToResponse);
    }
    
    @Override
    public CommentResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        return mapToResponse(comment);
    }
    
    @Override
    public long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }
    
    private CommentResponse mapToResponse(Comment comment) {
        User user = userRepository.findById(comment.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .user(CommentResponse.CommentUserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .avatarUrl(user.getAvatarUrl())
                        .build())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}