package com.example.chatroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ChatroomApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatroomApplication.class, args);
    }
}
