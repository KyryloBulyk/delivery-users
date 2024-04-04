package kyrylo.delivery.com.deliveryusersmicroservice.contollers;

import jakarta.validation.Valid;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException.EmailNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        logger.info("UserController initialized");
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Request to get all users");
        List<User> users = userService.getAllUsers();
        logger.info("Returning {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        logger.info("Request to get user by ID: {}", userId);
        try {
            User user = userService.getUserById(userId);
            logger.info("Found user with ID: {}", userId);
            return user;
        } catch (Exception e) {
            logger.error("User not found with ID: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/exists/{userId}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long userId) {
        logger.info("Checking if user exists with ID: {}", userId);
        boolean exists = userService.existsById(userId);
        logger.info("User exists with ID {}: {}", userId, exists);
        return ResponseEntity.ok(exists);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Request to update user with ID: {}", userId);
        try {
            User updatedUser = userService.updateUser(userId, registerRequest);
            logger.info("User with ID: {} updated successfully", userId);
            return updatedUser;
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", userId, e);
            throw e;
        }
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        logger.info("Request to delete user with ID: {}", userId);
        try {
            userService.deleteUser(userId);
            logger.info("User with ID: {} deleted successfully", userId);
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/email/{email}")
    public void existsByEmail(@PathVariable String email) {
        logger.info("Checking existence of email: {}", email);
        try {
            if (!userService.existsByEmail(email)) {
                logger.warn("Email not found: {}", email);
                throw new EmailNotFoundException(email);
            }
            logger.info("Email exists: {}", email);
        } catch (Exception e) {
            logger.error("Error checking email: {}", email, e);
            throw e;
        }
    }
}
