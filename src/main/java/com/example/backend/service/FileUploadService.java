
package com.example.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    
    /**
     * Upload file và trả về URL
     */
    String uploadFile(MultipartFile file);
    
    /**
     * Xóa file
     */
    void deleteFile(String fileUrl);
    
    /**
     * Validate file (type, size)
     */
    void validateFile(MultipartFile file);
}