package com.exemplo.chat.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class ChatController {

    @GetMapping("/status")
    public String getWebSocketStatus() {
        return "WebSocket está ativo!";
    }
}
