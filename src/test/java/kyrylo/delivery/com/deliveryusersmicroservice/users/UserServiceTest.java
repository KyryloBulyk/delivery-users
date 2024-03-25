package kyrylo.delivery.com.deliveryusersmicroservice.users;

import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RoleRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.UserRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetailsService userDetailsService;

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
        assertEquals("user1", users.get(0).getUsername());
        assertEquals("ROLE_ADMIN", users.get(1).getRole().getName());
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() {
        User user = new User(1L, "user1", "password1", "user1@example.com", new Role(1L, "ROLE_USER"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals("user1", foundUser.get().getUsername());
    }

    @Test
    void getUserById_NonExistingUser_ReturnsEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(1L);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void registerUser_NewUser_ReturnsUser() {
        RegisterRequest registerRequest = new RegisterRequest("newUser", "password", "newUser@example.com", "ROLE_USER");
        Role role = new Role(1L, "ROLE_USER");
        User savedUser = new User(1L, "newUser", "encodedPassword", "newUser@example.com", role);

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("newUser@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void updateUser_ExistingUser_Success() {
        Long userId = 1L;
        RegisterRequest updatedUser = new RegisterRequest("updatedUser", "newPassword", "updatedUser@example.com", "ROLE_ADMIN");
        User existingUser = new User(1L, "user1", "password1", "user1@example.com", new Role(1L, "ROLE_USER"));
        Role newRole = new Role(2L, "ROLE_ADMIN");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(newRole));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(userId, updatedUser);

        assertThat(result.getUsername()).isEqualTo(updatedUser.username());
        assertThat(result.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(result.getEmail()).isEqualTo(updatedUser.email());
        assertThat(result.getRole().getName()).isEqualTo(updatedUser.roleName());
    }

    @Test
    void updateUser_NonExistingUser_ReturnsNull() {
        Long userId = 99L;
        RegisterRequest updatedUser = new RegisterRequest("updatedUser", "newPassword", "updatedUser@example.com", "ROLE_ADMIN");

        when(userRepository.existsById(userId)).thenReturn(false);

        User result = userService.updateUser(userId, updatedUser);

        assertThat(result).isNull();
    }

    @Test
    void deleteUser_ExistingUser_Success() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        boolean result = userService.deleteUser(userId);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_NonExistingUser_Failure() {
        Long userId = 99L;

        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = userService.deleteUser(userId);

        assertThat(result).isFalse();
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void registerUser_ConflictUsernameOrEmail_ReturnsNull() {
        RegisterRequest newUser = new RegisterRequest("existingUser", "password", "existingEmail@example.com", "ROLE_USER");

        when(userRepository.existsByUsername(newUser.username())).thenReturn(true);
        when(userRepository.existsByEmail(newUser.email())).thenReturn(true);

        User result = userService.registerUser(newUser);

        assertThat(result).isNull();
    }

    @Test
    void loadUserByUsername_ExistingUser_ReturnsUserDetails() {
        String username = "existingUser";
        UserDetails mockUserDetails = mock(UserDetails.class);

        when(userDetailsService.loadUserByUsername(username)).thenReturn(mockUserDetails);

        UserDetails result = userService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(mockUserDetails, result);
        verify(userDetailsService, times(1)).loadUserByUsername(username);
    }

    // Тестування методу existsByEmail
    @Test
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        String email = "existingEmail@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userService.existsByEmail(email);

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        String email = "nonExistingEmail@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean result = userService.existsByEmail(email);

        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail(email);
    }

}

