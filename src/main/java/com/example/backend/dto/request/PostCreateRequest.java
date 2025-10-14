
package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {
    
    @Size(max = 2200, message = "Caption không được vượt quá 2200 ký tự")
    private String caption;
    
    // imageUrl sẽ được xử lý từ file upload, không cần validation ở đây
}