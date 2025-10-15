
package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request DTO để gửi tin nhắn
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {
    
    @NotNull(message = "Recipient ID is required")
    private Long recipientId;  // ID của người nhận
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String messageType = "text";  // text, image, video, file
}