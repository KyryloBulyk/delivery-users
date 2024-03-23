package kyrylo.delivery.com.deliveryusersmicroservice.Services;

import kyrylo.delivery.com.deliveryusersmicroservice.DTO.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.DTO.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.Entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.Repositories.RoleRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.Repositories.UserRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.Entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User updateUser(Long userId, RegisterRequest updatedUser) {
        if(!userRepository.existsById(userId)) return null;

        User existingUser = userRepository.findById(userId).get();

        existingUser.setUsername(updatedUser.username());
        existingUser.setPassword(updatedUser.password());
        existingUser.setEmail(updatedUser.email());

        Role role = roleRepository.findByName(updatedUser.roleName())
                .orElseThrow(() -> new RuntimeException("Role not found."));

        existingUser.setRole(role);

        return userRepository.save(existingUser);
    }

    public boolean deleteUser(Long userId) {
        if(!userRepository.existsById(userId)) return false;

        userRepository.deleteById(userId);

        return true;
    }

    public User registerUser(RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.username()) || userRepository.existsByEmail(registerRequest.email())) {
            return null;
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setEmail(registerRequest.email());

        Role role = roleRepository.findByName(registerRequest.roleName())
                .orElseThrow(() -> new RuntimeException("Role not found."));

        user.setRole(role);

        return userRepository.save(user);
    }


    public Optional<User> loginUser(AuthRequest authRequest) {
        return userRepository.findByUsername(authRequest.username())
                .filter(user -> passwordEncoder.matches(authRequest.password(), user.getPassword()));
    }

}
