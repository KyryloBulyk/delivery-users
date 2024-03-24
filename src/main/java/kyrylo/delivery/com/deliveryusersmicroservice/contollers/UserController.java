package kyrylo.delivery.com.deliveryusersmicroservice.contollers;

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

    @GetMapping("/email/{email}")
    public ResponseEntity<String> existsByEmail(@PathVariable String email) {
        boolean userIsExists = userService.existsByEmail(email);

        if(userIsExists)
            return new ResponseEntity<>("Email is found", HttpStatus.OK);

        return new ResponseEntity<>("User don't found", HttpStatus.BAD_REQUEST);
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


}
