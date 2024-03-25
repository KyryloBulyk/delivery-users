package kyrylo.delivery.com.deliveryusersmicroservice.auth;

import kyrylo.delivery.com.deliveryusersmicroservice.services.AuthService;
import kyrylo.delivery.com.deliveryusersmicroservice.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetails = new User("user", "password", Collections.emptyList());
    }

    @Test
    void generateToken_CallsJwtService() {
        when(jwtService.generateToken(userDetails)).thenReturn("fakeToken");

        String token = authService.generateToken(userDetails);

        verify(jwtService, times(1)).generateToken(userDetails);
        assertEquals("fakeToken", token);
    }

    @Test
    void extractUsername_CallsJwtService() {
        String fakeToken = "fakeToken";
        when(jwtService.extractUsernameWithoutValidation(fakeToken)).thenReturn("user");

        String username = authService.extractUsername(fakeToken);

        verify(jwtService, times(1)).extractUsernameWithoutValidation(fakeToken);
        assertEquals("user", username);
    }

    @Test
    void validateToken_ValidatesUsingJwtService() throws Exception {
        String fakeToken = "fakeToken";
        when(jwtService.extractUsername(fakeToken)).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);

        authService.validateToken(fakeToken);

        verify(jwtService, times(1)).validateToken(fakeToken, userDetails);
    }


}
