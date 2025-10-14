
package com.example.backend.service;

import com.example.backend.dto.request.PostCreateRequest;
import com.example.backend.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    
    // Tạo bài post mới
    PostResponse createPost(PostCreateRequest request, MultipartFile image, Long currentUserId);
    
    // Lấy chi tiết một bài post
    PostResponse getPostById(Long postId, Long currentUserId);
    
    // Lấy tất cả bài post của một user
    Page<PostResponse> getUserPosts(Long userId, Long currentUserId, Pageable pageable);
    
    // Lấy feed (bài post của user và những người user follow)
    Page<PostResponse> getFeedPosts(Long currentUserId, Pageable pageable);
    
    // Xóa bài post
    void deletePost(Long postId, Long currentUserId);
    
    // Cập nhật caption
    PostResponse updatePostCaption(Long postId, String caption, Long currentUserId);
    
    // Tìm kiếm post theo caption
    Page<PostResponse> searchPosts(String keyword, Long currentUserId, Pageable pageable);
}