package com.example.chatroom.service;

import com.example.chatroom.dto.RoomRequest;
import com.example.chatroom.dto.RoomResponse;
import com.example.chatroom.exception.ResourceNotFoundException;
import com.example.chatroom.model.Room;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.RoomRepository;
import com.example.chatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public RoomResponse createRoom(RoomRequest request, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Room room = Room.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(creator)
                .build();

        return toResponse(roomRepository.save(room));
    }

    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getDescription(),
                room.getCreatedBy().getUsername(),
                room.getCreatedAt()
        );
    }
}
