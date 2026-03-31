package com.example.chatroom.controller;

import com.example.chatroom.dto.MessageResponse;
import com.example.chatroom.dto.RoomRequest;
import com.example.chatroom.dto.RoomResponse;
import com.example.chatroom.exception.ResourceNotFoundException;
import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Room management")
@SecurityRequirement(name = "bearerAuth")
public class RoomController {

    private final RoomService roomService;
    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Get all public rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new room")
    public ResponseEntity<RoomResponse> createRoom(
            @Valid @RequestBody RoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.createRoom(request, userDetails.getUsername()));
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Get message history for a room")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long id) {
        roomService.getRoomById(id); // validate room exists
        return ResponseEntity.ok(messageService.getMessagesByRoom(id));
    }
}
