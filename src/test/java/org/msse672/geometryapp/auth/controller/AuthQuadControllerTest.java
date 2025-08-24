package org.msse672.geometryapp.auth.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthQuadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    void testPostQuadWithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/quad/type")
                        .param("sideA", "2")
                        .param("sideB", "2")
                        .param("sideC", "2")
                        .param("sideD", "2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testPostQuadWithAuth_ShouldReturn200() throws Exception {
        String token = mockMvc.perform(post("/auth/authenticate")
                        .param("username", "admin")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(post("/quad/type")
                        .header("Auth-Token", token)
                        .param("sideA", "2")
                        .param("sideB", "2")
                        .param("sideC", "2")
                        .param("sideD", "2"))
                .andExpect(status().isOk());
    }
}
