package kyrylo.delivery.com.deliveryusersmicroservice.Services;

import kyrylo.delivery.com.deliveryusersmicroservice.DTO.UserLoginDTO;
import kyrylo.delivery.com.deliveryusersmicroservice.DTO.UserRegistrationDTO;
import kyrylo.delivery.com.deliveryusersmicroservice.Entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.Repositories.RoleRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.Repositories.UserRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.Role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> updateUser(Long userId, User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setPassword(updatedUser.getPassword());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            return userRepository.save(user);
        });
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User registerUser(UserRegistrationDTO registrationDTO) {
        if(userRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            return null;
        }
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(registrationDTO.getPassword());
        user.setEmail(registrationDTO.getEmail());
        Role role = roleRepository.findByName(registrationDTO.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found."));
        user.setRole(role);
        return userRepository.save(user);
    }

    public Optional<User> loginUser(UserLoginDTO loginDTO) {
        return userRepository.findByUsername(loginDTO.getUsername())
                .filter(user -> (loginDTO.getPassword().equals(user.getPassword())));
    }
}
