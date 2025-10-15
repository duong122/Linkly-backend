
package com.example.backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event được publish khi có user mới follow
 * Dùng để tạo notification
 */
@Getter
public class NewFollowerEvent extends ApplicationEvent {

    private final Long followerId;  // User đang follow
    private final Long followingId; // User được follow

    public NewFollowerEvent(Object source, Long followerId, Long followingId) {
        super(source);
        this.followerId = followerId;
        this.followingId = followingId;
    }
}