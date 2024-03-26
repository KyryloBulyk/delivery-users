package kyrylo.delivery.com.deliveryusersmicroservice.refreshToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import kyrylo.delivery.com.deliveryusersmicroservice.entities.RefreshToken;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RefreshTokenRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.UserRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByUsername() {
        String username = "testUser";
        RefreshToken refreshToken = new RefreshToken();
        when(refreshTokenRepository.findByUser_Username(username)).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = refreshTokenService.findByUsername(username);

        verify(refreshTokenRepository).findByUser_Username(username);
        assertTrue(result.isPresent());
        assertEquals(refreshToken, result.get());
    }

    @Test
    public void testVerifyExpiration_WithValidToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(1000 * 604800));

        assertDoesNotThrow(() -> refreshTokenService.verifyExpiration(refreshToken));
    }

    @Test
    public void testVerifyExpiration_WithExpiredToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().minusMillis(1000));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.verifyExpiration(refreshToken);
        });

        assertEquals(refreshToken.getToken() + " Refresh token was expired. Please make a new signing request", exception.getMessage());
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    public void testDeleteByUsername_WithExistingToken() {
        String username = "userToDelete";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(new User());
        refreshToken.setToken(UUID.randomUUID().toString());

        when(refreshTokenRepository.findByUser_Username(username)).thenReturn(Optional.of(refreshToken));

        refreshTokenService.deleteByUsername(username);

        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    public void testDeleteByUsername_WithNoTokenFound() {
        String username = "nonExistingUser";
        when(refreshTokenRepository.findByUser_Username(username)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> refreshTokenService.deleteByUsername(username));
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }


    @Test
    public void testCreateRefreshToken_CreateNewToken() {
        User user = new User();
        user.setUsername("Example User");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        RefreshToken createdRefreshToken = new RefreshToken();
        createdRefreshToken.setToken("CreateToken");

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(createdRefreshToken);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertEquals("CreateToken", refreshToken.getToken());
    }

    @Test
    public void testUpdateRefreshToken_UpdatingOldRefreshToken() {
        RefreshToken oldRefreshToken = new RefreshToken();
        oldRefreshToken.setToken("refreshToken");

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("newRefreshToken");

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(newRefreshToken);

        RefreshToken refreshToken = refreshTokenService.updateRefreshToken(oldRefreshToken);

        assertEquals("newRefreshToken", refreshToken.getToken());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }




}

