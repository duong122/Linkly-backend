
package com.example.backend.service.impl;

import com.example.backend.dto.request.MessageRequest;
import com.example.backend.dto.response.ConversationResponse;
import com.example.backend.dto.response.MessageResponse;
import com.example.backend.entity.*;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.*;
import com.example.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MessageResponse sendMessage(Long senderId, MessageRequest messageRequest) {
        log.info("User {} sending message to user {}", senderId, messageRequest.getRecipientId());

        // Kiểm tra không thể gửi tin cho chính mình
        if (senderId.equals(messageRequest.getRecipientId())) {
            throw new BadRequestException("Cannot send message to yourself");
        }

        // Kiểm tra users tồn tại
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User recipient = userRepository.findById(messageRequest.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        // Tìm hoặc tạo conversation
        Conversation conversation = getOrCreateConversation(senderId, messageRequest.getRecipientId());

        // Tạo message
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(messageRequest.getContent())
                .messageType(messageRequest.getMessageType())
                .build();

        message = messageRepository.save(message);

        // Cập nhật updatedAt của conversation
        conversation.setUpdatedAt(message.getCreatedAt());
        conversationRepository.save(conversation);

        log.info("Message sent successfully: {}", message.getId());

        return mapToMessageResponse(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConversationResponse> getConversations(Long userId, int page, int size) {
        log.info("Getting conversations for user {}", userId);

        // Kiểm tra user tồn tại
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Conversation> conversations = conversationRepository.findByUserId(userId, pageable);

        return conversations.map(conversation -> mapToConversationResponse(conversation, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(Long conversationId, Long userId, int page, int size) {
        log.info("User {} getting messages from conversation {}", userId, conversationId);

        // Kiểm tra conversation tồn tại
        if (!conversationRepository.existsById(conversationId)) {
            throw new ResourceNotFoundException("Conversation not found");
        }

        // Kiểm tra user có quyền truy cập conversation không
        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new ForbiddenException("You do not have access to this conversation");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Message> messages = messageRepository.findByConversationId(conversationId, pageable);

        return messages.map(this::mapToMessageResponse);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        log.info("User {} attempting to delete message {}", userId, messageId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // Chỉ người gửi mới có thể xóa tin nhắn
        if (!message.getSender().getId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own messages");
        }

        messageRepository.delete(message);
        log.info("Message {} deleted successfully", messageId);
    }

    /**
     * Helper: Tìm hoặc tạo conversation giữa 2 users
     */
    private Conversation getOrCreateConversation(Long userId1, Long userId2) {
        // Tìm conversation hiện có
        Conversation conversation = conversationRepository.findConversationBetweenUsers(userId1, userId2);

        if (conversation != null) {
            log.debug("Found existing conversation: {}", conversation.getId());
            return conversation;
        }

        // Tạo conversation mới
        conversation = new Conversation();
        conversation = conversationRepository.save(conversation);

        // Thêm participants
        User user1 = userRepository.getReferenceById(userId1);
        User user2 = userRepository.getReferenceById(userId2);

        ConversationParticipant participant1 = ConversationParticipant.builder()
                .user(user1)
                .conversation(conversation)
                .build();

        ConversationParticipant participant2 = ConversationParticipant.builder()
                .user(user2)
                .conversation(conversation)
                .build();

        participantRepository.save(participant1);
        participantRepository.save(participant2);

        log.info("Created new conversation: {}", conversation.getId());
        return conversation;
    }

    /**
     * Helper: Map Message entity sang MessageResponse
     */
    private MessageResponse mapToMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .senderAvatarUrl(message.getSender().getAvatarUrl())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .createdAt(message.getCreatedAt())
                .build();
    }

    /**
     * Helper: Map Conversation entity sang ConversationResponse
     */
    private ConversationResponse mapToConversationResponse(Conversation conversation, Long currentUserId) {
        // Lấy participants
        List<ConversationParticipant> participants = participantRepository.findByConversationId(conversation.getId());

        // Tìm user còn lại (không phải current user)
        User otherUser = participants.stream()
                .map(ConversationParticipant::getUser)
                .filter(user -> !user.getId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        // Lấy tin nhắn cuối cùng
        MessageResponse lastMessage = messageRepository.findLastMessageByConversationId(conversation.getId())
                .map(this::mapToMessageResponse)
                .orElse(null);

        return ConversationResponse.builder()
                .id(conversation.getId())
                .otherUserId(otherUser != null ? otherUser.getId() : null)
                .otherUsername(otherUser != null ? otherUser.getUsername() : null)
                .otherUserAvatarUrl(otherUser != null ? otherUser.getAvatarUrl() : null)
                .lastMessage(lastMessage)
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }
}