package com.exemplo.chat.service;

import com.exemplo.chat.handler.ChatWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

@Service
public class ChatService {

    @Autowired
    private ChatWebSocketHandler chatHandler;

    public void printActiveSessions() {
        Set<WebSocketSession> activeSessions = chatHandler.getSessions();
        for (WebSocketSession session : activeSessions) {
            System.out.println("Sessão ativa: " + session.getId());
        }
    }
}
