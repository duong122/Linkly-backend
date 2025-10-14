
package com.example.backend.service.impl;

import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.FileUploadException;
import com.example.backend.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${file.max-size:5242880}") // 5MB default
    private long maxFileSize;
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    @Override
    public String uploadFile(MultipartFile file) {
        validateFile(file);
        
        try {
            // Tạo thư mục upload nếu chưa có
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Tạo tên file unique
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Lưu file
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("File uploaded successfully: {}", newFilename);
            
            // Trả về relative path
            return "/uploads/" + newFilename;
            
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new FileUploadException("Không thể upload file: " + e.getMessage());
        }
    }
    
    @Override
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl != null && fileUrl.startsWith("/uploads/")) {
                String filename = fileUrl.substring("/uploads/".length());
                Path filePath = Paths.get(uploadDir).resolve(filename);
                Files.deleteIfExists(filePath);
                log.info("File deleted successfully: {}", filename);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            // Không throw exception để không ảnh hưởng đến việc xóa post
        }
    }
    
    @Override
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File không được để trống");
        }
        
        // Kiểm tra kích thước
        if (file.getSize() > maxFileSize) {
            throw new BadRequestException("File vượt quá kích thước cho phép: " + (maxFileSize / 1024 / 1024) + "MB");
        }
        
        // Kiểm tra loại file
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Chỉ chấp nhận file ảnh: JPG, JPEG, PNG, GIF, WEBP");
        }
        
        // Kiểm tra tên file
        String filename = file.getOriginalFilename();
        if (filename == null || filename.contains("..")) {
            throw new BadRequestException("Tên file không hợp lệ");
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex);
    }
}