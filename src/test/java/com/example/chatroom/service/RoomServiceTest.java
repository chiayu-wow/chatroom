package com.example.chatroom.service;

import com.example.chatroom.dto.RoomRequest;
import com.example.chatroom.dto.RoomResponse;
import com.example.chatroom.exception.ResourceNotFoundException;
import com.example.chatroom.model.Room;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.RoomRepository;
import com.example.chatroom.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock RoomRepository roomRepository;
    @Mock UserRepository userRepository;
    @InjectMocks RoomService roomService;

    private User buildUser(String username) {
        return User.builder().id(1L).username(username).email(username + "@test.com").password("pw").build();
    }

    private Room buildRoom(Long id, String name, User creator) {
        return Room.builder().id(id).name(name).description("desc").createdBy(creator)
                .createdAt(LocalDateTime.now()).build();
    }

    @Test
    void getAllRooms_shouldReturnMappedResponses() {
        User user = buildUser("alice");
        Room room = buildRoom(1L, "general", user);
        when(roomRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(room));

        List<RoomResponse> result = roomService.getAllRooms();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("general");
        assertThat(result.get(0).getCreatedBy()).isEqualTo("alice");
    }

    @Test
    void createRoom_shouldSaveAndReturnResponse() {
        User user = buildUser("alice");
        RoomRequest req = new RoomRequest();
        req.setName("general");
        req.setDescription("main room");

        Room saved = buildRoom(1L, "general", user);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(roomRepository.save(any())).thenReturn(saved);

        RoomResponse response = roomService.createRoom(req, "alice");

        assertThat(response.getName()).isEqualTo("general");
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void getRoomById_shouldThrow_whenNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
