package kyrylo.delivery.com.deliveryusersmicroservice.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.JwtResponse;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;
    private User sampleUser;
    private static Long userId;

    @BeforeAll
    public static void setUpOnce(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "password123", "useremail@example.com", "ROLE_ADMIN");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        User registeredUser = objectMapper.readValue(registerResult.getResponse().getContentAsString(), User.class);
        userId = registeredUser.getUserId();
    }

    @BeforeEach
    void loginBeforeEachTest(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        AuthRequest authRequest = new AuthRequest("username", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(response, JwtResponse.class);
        this.jwtToken = jwtResponse.accessToken();

        sampleUser = new User();
        sampleUser.setUserId(userId);
        sampleUser.setUsername("username");
        sampleUser.setEmail("useremail@example.com");
        sampleUser.setPassword("password123");
    }

    @Test
    @Order(1)
    public void getAllUsers_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + this.jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("username"));
    }

    @Test
    @Order(2)
    public void getUserById_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/{userId}", sampleUser.getUserId())
                        .header("Authorization", "Bearer " + this.jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(sampleUser.getUsername()));
    }

    @Test
    @Order(3)
    public void existsByEmail_EmailExists_ReturnsOk() throws Exception {
        String existingEmail = "useremail@example.com";
        mockMvc.perform(get("/api/users/email/{email}", existingEmail)
                        .header("Authorization", "Bearer " + this.jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void existsByEmail_EmailDoesNotExist_ReturnsNotFound() throws Exception {
        String nonExistingEmail = "nonexisting@example.com";
        mockMvc.perform(get("/api/users/email/{email}", nonExistingEmail)
                        .header("Authorization", "Bearer " + this.jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void updateUser_ShouldUpdateUser() throws Exception {
        String updatedEmail = "updatedemail@example.com";
        RegisterRequest updateRequest = new RegisterRequest(sampleUser.getUsername(), sampleUser.getPassword(), updatedEmail, "ROLE_USER");

        mockMvc.perform(put("/api/users/{userId}", sampleUser.getUserId())
                        .header("Authorization", "Bearer " + this.jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(updatedEmail));
    }

    @Test
    @Order(6)
    public void deleteUser_ShouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}", sampleUser.getUserId())
                        .header("Authorization", "Bearer " + this.jwtToken))
                .andExpect(status().isOk());
    }


}

