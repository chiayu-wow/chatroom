package com.example.chatroom.controller;

import com.example.chatroom.BaseIntegrationTest;
import com.example.chatroom.dto.LoginRequest;
import com.example.chatroom.dto.RegisterRequest;
import com.example.chatroom.dto.RoomRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
class RoomControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String jwt;

    @BeforeEach
    void obtainJwt() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("roomtestuser");
        reg.setEmail("roomtest@example.com");
        reg.setPassword("password123");

        try {
            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reg)));
        } catch (Exception ignored) {}

        LoginRequest login = new LoginRequest();
        login.setUsername("roomtestuser");
        login.setPassword("password123");

        MvcResult result = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        jwt = objectMapper.readTree(body).get("token").asText();
    }

    @Test
    void getRooms_shouldReturn200_withJwt() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/rooms")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    void getRooms_shouldReturn403_withoutJwt() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/rooms"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void createRoom_shouldReturn201_andRoomDetails() throws Exception {
        RoomRequest req = new RoomRequest();
        req.setName("test-room");
        req.setDescription("a test room");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/rooms")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("test-room"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.createdBy").value("roomtestuser"));
    }

    @Test
    void getMessages_shouldReturn404_whenRoomNotExist() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/rooms/99999/messages")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }
}
