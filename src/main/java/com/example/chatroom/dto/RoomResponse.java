package com.example.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String name;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
}
