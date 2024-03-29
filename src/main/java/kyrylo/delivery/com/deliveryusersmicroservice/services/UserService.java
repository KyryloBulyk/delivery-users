package kyrylo.delivery.com.deliveryusersmicroservice.services;

import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.roleExceptions.RoleNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException.UserNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RoleRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.UserRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User updateUser(Long userId, RegisterRequest updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        existingUser.setUsername(updatedUser.username());
        existingUser.setPassword(passwordEncoder.encode(updatedUser.password()));
        existingUser.setEmail(updatedUser.email());

        Role role = roleRepository.findByName(updatedUser.roleName())
                .orElseThrow(() -> new RoleNotFoundException(updatedUser.roleName()));

        existingUser.setRole(role);

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {
        if(!userRepository.existsById(userId))
            throw new UserNotFoundException(userId);

        userRepository.deleteById(userId);

    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
