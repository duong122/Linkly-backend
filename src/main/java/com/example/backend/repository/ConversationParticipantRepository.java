
package com.example.backend.repository;

import com.example.backend.entity.ConversationParticipant;
import com.example.backend.entity.ConversationParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho ConversationParticipant entity
 */
@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, ConversationParticipantId> {

    /**
     * Lấy participants của một conversation
     */
    @Query("SELECT cp FROM ConversationParticipant cp WHERE cp.conversation.id = :conversationId")
    List<ConversationParticipant> findByConversationId(@Param("conversationId") Long conversationId);

    /**
     * Kiểm tra user có trong conversation không
     */
    @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END " +
           "FROM ConversationParticipant cp " +
           "WHERE cp.conversation.id = :conversationId AND cp.user.id = :userId")
    boolean existsByConversationIdAndUserId(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );
}