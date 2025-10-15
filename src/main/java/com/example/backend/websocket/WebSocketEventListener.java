
package com.example.backend.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

/**
 * Listener để track WebSocket connections và disconnections
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Event khi user connect WebSocket
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        
        if (user != null) {
            log.info("✅ WebSocket CONNECTED: User {} (Session: {})", 
                    user.getName(), headerAccessor.getSessionId());
            
            // Có thể broadcast user online status
            // messagingTemplate.convertAndSend("/topic/user.online", user.getName());
        }
    }

    /**
     * Event khi user disconnect WebSocket
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();
        
        if (user != null) {
            log.info("❌ WebSocket DISCONNECTED: User {} (Session: {})", 
                    user.getName(), headerAccessor.getSessionId());
            
            // Có thể broadcast user offline status
            // messagingTemplate.convertAndSend("/topic/user.offline", user.getName());
        }
    }
}