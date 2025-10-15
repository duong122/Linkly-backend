
package com.example.backend.controller;

import com.example.backend.dto.request.MessageRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.ConversationResponse;
import com.example.backend.dto.response.MessageResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.security.UserPrincipal;
import com.example.backend.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý messaging
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * Gửi tin nhắn
     * POST /api/messages
     */
    @PostMapping
    public ResponseEntity<ApiResponse> sendMessage(
            @Valid @RequestBody MessageRequest messageRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        MessageResponse messageResponse = messageService.sendMessage(
                currentUser.getId(),
                messageRequest
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Message sent successfully")
                        .data(messageResponse)
                        .build()
        );
    }

    /**
     * Lấy danh sách conversations
     * GET /api/messages/conversations?page=0&size=20
     */
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse> getConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Page<ConversationResponse> conversations = messageService.getConversations(
                currentUser.getId(),
                page,
                size
        );

        PageResponse<ConversationResponse> pageResponse = PageResponse.<ConversationResponse>builder()
                .content(conversations.getContent())
                .pageNumber(conversations.getNumber())
                .pageSize(conversations.getSize())
                .totalElements(conversations.getTotalElements())
                .totalPages(conversations.getTotalPages())
                .last(conversations.isLast())
                .build();

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Conversations retrieved successfully")
                        .data(pageResponse)
                        .build()
        );
    }

    /**
     * Lấy tin nhắn trong conversation
     * GET /api/messages/conversations/{conversationId}?page=0&size=50
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ApiResponse> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Page<MessageResponse> messages = messageService.getMessages(
                conversationId,
                currentUser.getId(),
                page,
                size
        );

        PageResponse<MessageResponse> pageResponse = PageResponse.<MessageResponse>builder()
                .content(messages.getContent())
                .pageNumber(messages.getNumber())
                .pageSize(messages.getSize())
                .totalElements(messages.getTotalElements())
                .totalPages(messages.getTotalPages())
                .last(messages.isLast())
                .build();

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Messages retrieved successfully")
                        .data(pageResponse)
                        .build()
        );
    }

    /**
     * Xóa tin nhắn
     * DELETE /api/messages/{messageId}
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse> deleteMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        messageService.deleteMessage(messageId, currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Message deleted successfully")
                        .build()
        );
    }
}