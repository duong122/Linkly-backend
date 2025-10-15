
package com.example.backend.service;

import com.example.backend.dto.request.MessageRequest;
import com.example.backend.dto.response.ConversationResponse;
import com.example.backend.dto.response.MessageResponse;
import org.springframework.data.domain.Page;

/**
 * Service interface cho Messaging
 */
public interface MessageService {

    /**
     * Gửi tin nhắn
     * @param senderId ID người gửi
     * @param messageRequest thông tin tin nhắn
     * @return MessageResponse
     */
    MessageResponse sendMessage(Long senderId, MessageRequest messageRequest);

    /**
     * Lấy danh sách conversations của user
     * @param userId ID của user
     * @param page số trang
     * @param size kích thước trang
     * @return Page of ConversationResponse
     */
    Page<ConversationResponse> getConversations(Long userId, int page, int size);

    /**
     * Lấy tin nhắn trong conversation
     * @param conversationId ID của conversation
     * @param userId ID của user (để check quyền truy cập)
     * @param page số trang
     * @param size kích thước trang
     * @return Page of MessageResponse
     */
    Page<MessageResponse> getMessages(Long conversationId, Long userId, int page, int size);

    /**
     * Xóa tin nhắn
     * @param messageId ID của tin nhắn
     * @param userId ID của user (chỉ có người gửi mới xóa được)
     */
    void deleteMessage(Long messageId, Long userId);
}