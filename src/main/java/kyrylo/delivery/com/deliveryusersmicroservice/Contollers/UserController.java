package kyrylo.delivery.com.deliveryusersmicroservice.Contollers;

import kyrylo.delivery.com.deliveryusersmicroservice.DTO.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.DTO.RegisterRequest;
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
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest);
        if(user == null) {
            return new ResponseEntity<>("Registration failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody RegisterRequest registerRequest) {
        User user = userService.updateUser(userId, registerRequest);
        if(user == null)
            return new ResponseEntity<>("User wasn't found", HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(user);

    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        if(!userService.deleteUser(userId)) {
            return new ResponseEntity<>("User wasn't found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("User was deleted", HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authRequest) {
        return userService.loginUser(authRequest)
                .map(user -> ResponseEntity.ok().body("Login successful"))
                .orElse(new ResponseEntity<>("Username or password is incorrect", HttpStatus.UNAUTHORIZED));
    }


}
