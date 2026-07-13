package com.mindmirror.backend.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signedInUserCanReadProfileSavePreferencesAndReadSummary() throws Exception {
        String token = register("account@example.com", "Account User", "SecurePass123!");

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("account@example.com"))
            .andExpect(jsonPath("$.displayName").value("Account User"));

        mockMvc.perform(put("/api/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"darkMode\":true,\"notifications\":true}"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/me/summary").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.routineDays").value(0))
            .andExpect(jsonPath("$.darkMode").value(true))
            .andExpect(jsonPath("$.notifications").value(true));
    }

    @Test
    void changingEmailRequiresCurrentPassword() throws Exception {
        String token = register("secure@example.com", "Secure User", "SecurePass123!");

        mockMvc.perform(put("/api/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"new@example.com\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("Current password is required to change email or password"));
    }

    private String register(String email, String displayName, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"%s\",\"displayName\":\"%s\",\"password\":\"%s\"}".formatted(email, displayName, password)))
            .andExpect(status().isCreated())
            .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("accessToken").asText();
    }
}
