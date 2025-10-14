
package com.example.backend.service.impl;

import com.example.backend.dto.request.PostCreateRequest;
import com.example.backend.dto.response.PostResponse;
import com.example.backend.entity.Post;
import com.example.backend.entity.User;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.PostMapper;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.LikeRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.FileUploadService;
import com.example.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FileUploadService fileUploadService;
    private final PostMapper postMapper;
    
    @Override
    public PostResponse createPost(PostCreateRequest request, MultipartFile image, Long currentUserId) {
        // Validate image
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Ảnh không được để trống");
        }
        
        // Tìm user
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));
        
        // Upload ảnh
        String imageUrl = fileUploadService.uploadFile(image);
        
        // Tạo post
        Post post = new Post();
        post.setUser(user);
        post.setCaption(request.getCaption());
        post.setImageUrl(imageUrl);
        
        Post savedPost = postRepository.save(post);
        
        return postMapper.toResponse(savedPost, 0L, 0L, false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post không tồn tại"));
        
        Long likeCount = likeRepository.countByPostId(postId);
        Long commentCount = commentRepository.countByPostId(postId);
        Boolean isLiked = currentUserId != null && likeRepository.existsByUserIdAndPostId(currentUserId, postId);
        
        return postMapper.toResponse(post, likeCount, commentCount, isLiked);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getUserPosts(Long userId, Long currentUserId, Pageable pageable) {
        // Kiểm tra user có tồn tại không
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User không tồn tại");
        }
        
        Page<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return posts.map(post -> {
            Long likeCount = likeRepository.countByPostId(post.getId());
            Long commentCount = commentRepository.countByPostId(post.getId());
            Boolean isLiked = currentUserId != null && 
                    likeRepository.existsByUserIdAndPostId(currentUserId, post.getId());
            
            return postMapper.toResponse(post, likeCount, commentCount, isLiked);
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getFeedPosts(Long currentUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findFeedPosts(currentUserId, pageable);
        
        return posts.map(post -> {
            Long likeCount = likeRepository.countByPostId(post.getId());
            Long commentCount = commentRepository.countByPostId(post.getId());
            Boolean isLiked = likeRepository.existsByUserIdAndPostId(currentUserId, post.getId());
            
            return postMapper.toResponse(post, likeCount, commentCount, isLiked);
        });
    }
    
    @Override
    public void deletePost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post không tồn tại"));
        
        // Kiểm tra quyền: chỉ owner mới được xóa
        if (!post.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền xóa bài post này");
        }
        
        // Xóa file ảnh
        fileUploadService.deleteFile(post.getImageUrl());
        
        // Xóa post (cascade sẽ tự động xóa likes và comments)
        postRepository.delete(post);
    }
    
    @Override
    public PostResponse updatePostCaption(Long postId, String caption, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post không tồn tại"));
        
        // Kiểm tra quyền
        if (!post.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa bài post này");
        }
        
        post.setCaption(caption);
        Post updatedPost = postRepository.save(post);
        
        Long likeCount = likeRepository.countByPostId(postId);
        Long commentCount = commentRepository.countByPostId(postId);
        Boolean isLiked = likeRepository.existsByUserIdAndPostId(currentUserId, postId);
        
        return postMapper.toResponse(updatedPost, likeCount, commentCount, isLiked);
    }
    
@Override
@Transactional(readOnly = true)
public Page<PostResponse> searchPosts(String keyword, Long currentUserId, Pageable pageable) {
    Page<Post> posts = postRepository.searchByCaption(keyword, pageable);  // ← ĐỔI TÊN METHOD
    
    return posts.map(post -> {
        Long likeCount = likeRepository.countByPostId(post.getId());
        Long commentCount = commentRepository.countByPostId(post.getId());
        Boolean isLiked = currentUserId != null && 
                likeRepository.existsByUserIdAndPostId(currentUserId, post.getId());
        
        return postMapper.toResponse(post, likeCount, commentCount, isLiked);
    });
}
}