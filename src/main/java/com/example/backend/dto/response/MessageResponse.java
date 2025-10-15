
package com.example.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Response DTO cho Message
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderUsername;
    private String senderAvatarUrl;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
}