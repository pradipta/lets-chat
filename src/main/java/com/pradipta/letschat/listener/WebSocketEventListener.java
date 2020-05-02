package com.pradipta.letschat.listener;

import com.pradipta.letschat.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @EventListener
    public void handleWebSocketConnectListener (SessionConnectedEvent event) {
        LOGGER.info("Connection recieved");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) stompHeaderAccessor.getSessionAttributes().get("username");

        if (username==null) {
            return;
        }

        LOGGER.info(username+" disconnected");

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(username+" disconnected");
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setSender(username);

        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
    }


}
