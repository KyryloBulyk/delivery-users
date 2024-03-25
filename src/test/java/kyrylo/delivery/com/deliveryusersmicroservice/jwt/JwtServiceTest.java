package kyrylo.delivery.com.deliveryusersmicroservice.jwt;

import kyrylo.delivery.com.deliveryusersmicroservice.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        userDetails = new User("user", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void validateToken_WithValidToken_ReturnsTrue() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void extractUsername_ReturnsCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        assertEquals("user", jwtService.extractUsername(token));
    }

    @Test
    void extractExpiration_ReturnsCorrectExpiration() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(jwtService.extractExpiration(token));
    }

    @Test
    void extractUsernameWithoutValidation_ValidToken_ReturnsUsername() {
        String fakeTokenPayload = "{\"sub\":\"testUser\"}";
        String encodedPayload = Base64.getUrlEncoder().encodeToString(fakeTokenPayload.getBytes());
        String fakeToken = "header." + encodedPayload + ".signature";

        String extractedUsername = jwtService.extractUsernameWithoutValidation(fakeToken);

        assertEquals("testUser", extractedUsername);
    }

    @Test
    void extractUsernameWithoutValidation_InvalidToken_ThrowsIllegalArgumentException() {
        String invalidToken = "invalid";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            jwtService.extractUsernameWithoutValidation(invalidToken);
        });

        String expectedMessage = "Invalid JWT token.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void extractUsernameWithoutValidation_EmptyToken_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            jwtService.extractUsernameWithoutValidation("");
        });

        String expectedMessage = "Token cannot be empty or null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}

