package com.exemplo.chat.dto;

import com.exemplo.chat.enums.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String sender;
    private String content;
    private MessageType type;
}