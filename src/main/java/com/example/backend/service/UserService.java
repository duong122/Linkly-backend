
package com.example.backend.service;

import com.example.backend.dto.request.PasswordChangeRequest;
import com.example.backend.dto.request.UserUpdateRequest;
import com.example.backend.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    
    UserResponse getCurrentUser(Long userId);
    
    UserResponse getUserByUsername(String username, Long currentUserId);
    
    UserResponse getUserById(Long userId, Long currentUserId);
    
    UserResponse updateUser(Long userId, UserUpdateRequest request);
    
    void changePassword(Long userId, PasswordChangeRequest request);
    
    List<UserResponse> searchUsers(String keyword, Long currentUserId);
}