package com.example.chatroom.repository;

import com.example.chatroom.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByOrderByCreatedAtDesc();
}
