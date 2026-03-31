package com.example.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private String content;
    private String sender;
    private Long roomId;
    private LocalDateTime createdAt;
}
