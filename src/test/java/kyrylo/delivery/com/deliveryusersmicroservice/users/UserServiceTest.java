package kyrylo.delivery.com.deliveryusersmicroservice.users;

import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException.UserNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RoleRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.UserRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.services.RefreshTokenService;
import kyrylo.delivery.com.deliveryusersmicroservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        User user1 = new User(1L, "user1", "password1", "user1@example.com", new Role(1L, "ROLE_USER"));
        User user2 = new User(2L, "user2", "password2", "user2@example.com", new Role(2L, "ROLE_ADMIN"));
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() {
        User user = new User(1L, "user1", "password1", "user1@example.com", new Role(1L, "ROLE_USER"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals("user1", foundUser.getUsername());
    }

    @Test
    void getUserById_NonExistingUser_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_ExistingUser_Success() {
        Long userId = 1L;
        RegisterRequest updatedUser = new RegisterRequest("updatedUser", "newPassword", "updatedUser@example.com", "ROLE_ADMIN");
        User existingUser = new User(userId, "user1", "password1", "user1@example.com", new Role(1L, "ROLE_USER"));
        Role newRole = new Role(2L, "ROLE_ADMIN");
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(newRole));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(userId, updatedUser);

        assertEquals("updatedUser", result.getUsername());
        assertEquals("encodedNewPassword", result.getPassword());
        assertEquals(newRole, result.getRole());
    }

    @Test
    void updateUser_NonExistingUser_ThrowsException() {
        Long userId = 99L;
        RegisterRequest updatedUser = new RegisterRequest("updatedUser", "newPassword", "updatedUser@example.com", "ROLE_ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updatedUser));
    }

    @Test
    void deleteUser_ExistingUser_Success() {
        Long userId = 1L;
        User user = new User();
        user.setUsername("username");
        user.setEmail("username@exaple.com");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(refreshTokenService).deleteByUsername(any(String.class));
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_NonExistingUser_ThrowsException() {
        Long userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void existsByEmail_EmailExists_ReturnsTrue() {
        String email = "user1@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userService.existsByEmail(email);

        assertTrue(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_EmailDoesNotExist_ReturnsFalse() {
        String email = "nonexisting@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean result = userService.existsByEmail(email);

        assertFalse(result);
        verify(userRepository).existsByEmail(email);
    }

}
