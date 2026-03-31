package com.example.chatroom.service;

import com.example.chatroom.dto.MessageResponse;
import com.example.chatroom.model.Message;
import com.example.chatroom.model.Room;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.MessageRepository;
import com.example.chatroom.repository.UserRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock MessageRepository messageRepository;
    @Mock UserRepository userRepository;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository, userRepository, new SimpleMeterRegistry());
    }

    @Test
    void saveMessageAsync_shouldPersistMessage() {
        User sender = User.builder().id(1L).username("alice").email("a@test.com").password("pw").build();
        Room room = Room.builder().id(1L).name("general").createdBy(sender).build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sender));
        when(messageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        messageService.saveMessageAsync("hello", "alice", room);

        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void getMessagesByRoom_shouldReturnOrderedResponses() {
        User sender = User.builder().id(1L).username("alice").email("a@test.com").password("pw").build();
        Room room = Room.builder().id(1L).name("general").createdBy(sender).build();
        Message msg = Message.builder().id(1L).content("hello").sender(sender).room(room)
                .createdAt(LocalDateTime.now()).build();

        when(messageRepository.findByRoomIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(msg));

        List<MessageResponse> result = messageService.getMessagesByRoom(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("hello");
        assertThat(result.get(0).getSender()).isEqualTo("alice");
    }
}
