package kyrylo.delivery.com.deliveryusersmicroservice.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.JwtResponse;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
    private Logger logger = LoggerFactory.getLogger(UserControllerIntegrationTest.class);

    @BeforeAll
    public static void setUpOnce(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper, @Autowired UserRepository userRepository) throws Exception {
        if (!alreadySetup) {
            RegisterRequest registerRequest = new RegisterRequest("username", "password", "useremail@example.com", "ROLE_ADMIN");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());

            alreadySetup = true;
        }
    }

    @BeforeEach
    void loginBeforeEachTest() throws Exception {
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
    }

//    @Test
//    public void getAllUsersTest() throws Exception {
//        User sampleUser = new User();
//        sampleUser.setUserId(1L); // Припустимо, ID користувача 1
//        // Тут ви можете встановити інші поля sampleUser
//
//        when(userService.getAllUsers()).thenReturn(Arrays.asList(sampleUser)); // Налаштування моку
//
//        mockMvc.perform(get("/api/users")
//                        .header("Authorization", "Bearer " + jwtToken)) // Використання токена
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$[0].id").value(sampleUser.getUserId()));
//    }
}

