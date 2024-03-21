package kyrylo.delivery.com.deliveryusersmicroservice.Contollers;

import kyrylo.delivery.com.deliveryusersmicroservice.DTO.UserLoginDTO;
import kyrylo.delivery.com.deliveryusersmicroservice.DTO.UserRegistrationDTO;
import kyrylo.delivery.com.deliveryusersmicroservice.Entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        User user = userService.registerUser(userRegistrationDTO);
        if(user == null) {
            return new ResponseEntity<>("Registration failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        return userService.updateUser(userId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        if(!userService.deleteUser(userId)) {
            return new ResponseEntity<>("User wasn't found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("User was deleted", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO loginDTO) {
        return userService.loginUser(loginDTO)
                .map(user -> ResponseEntity.ok().body("Login successful"))
                .orElse(new ResponseEntity<>("Username or password is incorrect", HttpStatus.UNAUTHORIZED));
    }
}
