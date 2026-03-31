package com.example.chatroom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class ChatroomApplicationTests extends BaseIntegrationTest {

    @Test
    void contextLoads() {
    }
}
