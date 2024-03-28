package kyrylo.delivery.com.deliveryusersmicroservice.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.JwtResponse;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static boolean alreadySetup = false;
    private String jwtToken;
    private User sampleUser;

    private static Long userId;
    private Logger logger = LoggerFactory.getLogger(UserControllerIntegrationTest.class);

    @BeforeAll
    public static void setUpOnce(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper, @Autowired UserRepository userRepository) throws Exception {
        if (!alreadySetup) {
            RegisterRequest registerRequest = new RegisterRequest("username", "password", "useremail@example.com", "ROLE_ADMIN");

            MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            User registeredUser = objectMapper.readValue(registerResult.getResponse().getContentAsString(), User.class);
            userId = registeredUser.getUserId();

            alreadySetup = true;
        }
    }

    @BeforeEach
    void loginBeforeEachTest(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper, @Autowired UserRepository userRepository) throws Exception {
        AuthRequest authRequest = new AuthRequest("username", "password");
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
        sampleUser.setPassword("password");
    }

    @Test
    public void getAllUsers_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + this.jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("username"));
    }

    @Test
    public void getUserById_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/{userId}", sampleUser.getUserId())
                        .header("Authorization", "Bearer " + this.jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(sampleUser.getUsername()));
    }

}

