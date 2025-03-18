package com.exemplo.chat.handler;

import com.exemplo.chat.exception.WebSocketException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component  // Adicionando o @Component para registrar como bean no Spring
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Long> lastActivityTime = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            System.out.println("Conexão WebSocket estabelecida: " + session.getId());
            sessions.put(session.getId(), session);
            lastActivityTime.put(session.getId(), System.currentTimeMillis());
            session.sendMessage(new TextMessage("Você entrou no chat!"));
        } catch (IOException e) {
            throw new WebSocketException("Erro ao estabelecer conexão WebSocket", e);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            System.out.println("Mensagem recebida: " + message.getPayload());
            lastActivityTime.put(session.getId(), System.currentTimeMillis());
            session.sendMessage(new TextMessage("Mensagem recebida: " + message.getPayload()));

            long idleTime = System.currentTimeMillis() - lastActivityTime.get(session.getId());
            if (idleTime > TimeUnit.SECONDS.toMillis(30)) {
                session.sendMessage(new TextMessage("ping"));
            }

        } catch (IOException e) {
            throw new WebSocketException("Erro ao processar a mensagem WebSocket", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            System.out.println("Conexão WebSocket fechada: " + session.getId());
            sessions.remove(session.getId());
            lastActivityTime.remove(session.getId());
        } catch (Exception e) {
            throw new WebSocketException("Erro ao fechar a conexão WebSocket", e);
        }
    }

    public Set<WebSocketSession> getSessions() {
        return Set.copyOf(sessions.values());
    }
}
