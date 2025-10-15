package com.example.backend.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key cho ConversationParticipant
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationParticipantId implements Serializable {

    private User user;
    private Conversation conversation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversationParticipantId that = (ConversationParticipantId) o;
        return Objects.equals(user, that.user) &&
               Objects.equals(conversation, that.conversation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, conversation);
    }
}