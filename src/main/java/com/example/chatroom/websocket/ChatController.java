package com.example.chatroom.websocket;

import com.example.chatroom.dto.ChatMessage;
import com.example.chatroom.dto.MessageResponse;
import com.example.chatroom.exception.ResourceNotFoundException;
import com.example.chatroom.model.Room;
import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final RoomService roomService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatMessage chatMessage,
            Principal principal
    ) {
        String username = principal.getName();
        log.info("WebSocket message: sender={} roomId={} timestamp={}", username, roomId, LocalDateTime.now());

        Room room = roomService.getRoomById(roomId);

        // Save to DB asynchronously
        messageService.saveMessageAsync(chatMessage.getContent(), username, room);

        // Broadcast to subscribers immediately
        MessageResponse response = new MessageResponse(
                null,
                chatMessage.getContent(),
                username,
                roomId,
                LocalDateTime.now()
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomId, response);
    }
}
