package kyrylo.delivery.com.deliveryusersmicroservice.roles;

import com.fasterxml.jackson.databind.ObjectMapper;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.JwtResponse;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
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
public class RoleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeAll
    public static void setUpOnce(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username2", "password123", "useremail2@example.com", "ROLE_ADMIN");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @BeforeEach
    void loginBeforeEachTest(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception  {
        AuthRequest authRequest = new AuthRequest("username2", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(response, JwtResponse.class);
        this.jwtToken = jwtResponse.accessToken();
    }

    @Test
    @Order(1)
    void getAllRoles_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/roles")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(2)
    void getRoleById_ShouldReturnRole() throws Exception {
        mockMvc.perform(get("/api/roles/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roleId").value(1));
    }

    @Test
    @Order(3)
    void createNewRole_ShouldCreateRole() throws Exception {
        Role newRole = new Role(null, "ROLE_NEW");

        mockMvc.perform(post("/api/roles")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRole)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ROLE_NEW"));
    }

    @Test
    @Order(4)
    void updateRole_ShouldUpdateRole() throws Exception {
        Role updatedRole = new Role(null, "ROLE_UPDATED");
        mockMvc.perform(put("/api/roles/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRole)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ROLE_UPDATED"));
        
    }

    @Test
    @Order(5)
    void deleteRole_ShouldDeleteRole() throws Exception {
        mockMvc.perform(delete("/api/roles/5")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

}
