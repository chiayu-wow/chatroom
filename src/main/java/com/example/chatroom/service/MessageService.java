package com.example.chatroom.service;

import com.example.chatroom.dto.MessageResponse;
import com.example.chatroom.model.Message;
import com.example.chatroom.model.Room;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.MessageRepository;
import com.example.chatroom.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final Counter messageCounter;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          MeterRegistry meterRegistry) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageCounter = Counter.builder("chat.messages.sent")
                .description("Total number of chat messages sent")
                .register(meterRegistry);
    }

    @Async("taskExecutor")
    public void saveMessageAsync(String content, String username, Room room) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Message message = Message.builder()
                .content(content)
                .sender(sender)
                .room(room)
                .build();

        messageRepository.save(message);
        messageCounter.increment();
        log.info("Message saved: sender={} roomId={}", username, room.getId());
    }

    public List<MessageResponse> getMessagesByRoom(Long roomId) {
        return messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId).stream()
                .map(this::toResponse)
                .toList();
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getSender().getUsername(),
                message.getRoom().getId(),
                message.getCreatedAt()
        );
    }
}
