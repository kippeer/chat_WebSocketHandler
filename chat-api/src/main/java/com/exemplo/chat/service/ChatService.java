package com.exemplo.chat.service;

import com.exemplo.chat.dto.MessageDTO;
import com.exemplo.chat.model.Message;
import com.exemplo.chat.enums.MessageType;
import com.exemplo.chat.exception.WebSocketException;
import com.exemplo.chat.handler.ChatWebSocketHandler;
import com.exemplo.chat.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ChatService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ChatWebSocketHandler chatHandler;
    
    @Autowired
    private MessageRepository messageRepository;
    
    public void handleMessage(WebSocketSession session, TextMessage textMessage) {
        try {
            MessageDTO messageDTO = objectMapper.readValue(textMessage.getPayload(), MessageDTO.class);
            
            // Validação básica
            if (messageDTO.getSender() == null || messageDTO.getSender().trim().isEmpty()) {
                throw new WebSocketException("Sender cannot be empty");
            }
            if (messageDTO.getContent() == null || messageDTO.getContent().trim().isEmpty()) {
                throw new WebSocketException("Message content cannot be empty");
            }
            
            Message message = new Message(
                UUID.randomUUID().toString(),
                messageDTO.getSender(),
                messageDTO.getContent(),
                LocalDateTime.now(),
                MessageType.CHAT
            );
            
            // Salvar mensagem no banco
            message = messageRepository.save(message);
            
            broadcastMessage(session, message);
        } catch (IOException e) {
            throw new WebSocketException("Error processing message", e);
        }
    }
    
    public void handleJoin(WebSocketSession session) {
        try {
            String sessionId = session.getId();
            Message joinMessage = new Message(
                UUID.randomUUID().toString(),
                "System",
                "User " + sessionId + " joined the chat",
                LocalDateTime.now(),
                MessageType.JOIN
            );
            
            // Salvar mensagem de join
            joinMessage = messageRepository.save(joinMessage);
            
            // Enviar histórico de mensagens
            sendMessageHistory(session);
            
            broadcastMessage(session, joinMessage);
        } catch (Exception e) {
            throw new WebSocketException("Error handling join", e);
        }
    }
    
    public void handleLeave(WebSocketSession session) {
        try {
            String sessionId = session.getId();
            Message leaveMessage = new Message(
                UUID.randomUUID().toString(),
                "System",
                "User " + sessionId + " left the chat",
                LocalDateTime.now(),
                MessageType.LEAVE
            );
            
            // Salvar mensagem de leave
            leaveMessage = messageRepository.save(leaveMessage);
            
            broadcastMessage(session, leaveMessage);
        } catch (Exception e) {
            throw new WebSocketException("Error handling leave", e);
        }
    }
    
    private void sendMessageHistory(WebSocketSession session) {
        try {
            messageRepository.findTop50ByOrderByTimestampDesc()
                .forEach(message -> {
                    try {
                        String messageJson = objectMapper.writeValueAsString(message);
                        session.sendMessage(new TextMessage(messageJson));
                    } catch (IOException e) {
                        throw new WebSocketException("Error sending message history", e);
                    }
                });
        } catch (Exception e) {
            throw new WebSocketException("Error retrieving message history", e);
        }
    }
    
    private void broadcastMessage(WebSocketSession sender, Message message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            TextMessage textMessage = new TextMessage(messageJson);
            
            for (WebSocketSession session : chatHandler.getSessions()) {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                } catch (IOException e) {
                    throw new WebSocketException("Error sending message to session: " + session.getId(), e);
                }
            }
        } catch (IOException e) {
            throw new WebSocketException("Error broadcasting message", e);
        }
    }
}