package com.exemplo.chat.controller;

import com.exemplo.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    @Autowired
    private ChatService chatService;
    
    // Endpoints REST adicionais se necessário
}