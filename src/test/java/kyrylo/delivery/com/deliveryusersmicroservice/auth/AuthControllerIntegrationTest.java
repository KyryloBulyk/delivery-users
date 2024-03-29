package kyrylo.delivery.com.deliveryusersmicroservice.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.JwtResponse;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
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
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "password123", "useremail@example.com", "ROLE_ADMIN");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        AuthRequest authRequest = new AuthRequest("username", "password123");
        String response = mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(response, JwtResponse.class);
        this.jwtToken = jwtResponse.accessToken();
    }

    @Test
    @Order(1)
    void registration_ShouldCreateUser() throws Exception {
        RegisterRequest newRegisterRequest = new RegisterRequest("newUser", "newPassword", "newemail@example.com", "ROLE_USER");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUser"));
    }

    @Test
    @Order(2)
    void getToken_ShouldReturnJwt() throws Exception {
        AuthRequest authRequest = new AuthRequest("username", "password123");
        mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @Order(3)
    void validateToken_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/auth/validateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new JwtResponse(jwtToken))))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void logout_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully."));
    }

    @Test
    @Order(5)
    void refreshAccessToken_ShouldReturnNewAccessToken() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
