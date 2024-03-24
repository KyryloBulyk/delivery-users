package kyrylo.delivery.com.deliveryusersmicroservice.contollers;

import kyrylo.delivery.com.deliveryusersmicroservice.dto.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.filter.JwtAuthFilter;
import kyrylo.delivery.com.deliveryusersmicroservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    private Logger logger =  LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {

        logger.info("In User Controller");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
