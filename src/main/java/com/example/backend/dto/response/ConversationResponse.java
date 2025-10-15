package com.example.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Response DTO cho Conversation
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponse {
    private Long id;
    private Long otherUserId;
    private String otherUsername;
    private String otherUserAvatarUrl;
    private MessageResponse lastMessage;
    private LocalDateTime updatedAt;
}