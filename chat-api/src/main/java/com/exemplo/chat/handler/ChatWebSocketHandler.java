package com.exemplo.chat.handler;

import com.exemplo.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    
    @Autowired
    private ChatService chatService;
    
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        chatService.handleMessage(session, message);
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        chatService.handleJoin(session);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        chatService.handleLeave(session);
    }
    
    public Set<WebSocketSession> getSessions() {
        return sessions;
    }
}