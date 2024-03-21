package kyrylo.delivery.com.deliveryusersmicroservice.Services;

import kyrylo.delivery.com.deliveryusersmicroservice.DTO.UserDTO;
import kyrylo.delivery.com.deliveryusersmicroservice.DTO.UserLoginDTO;
import kyrylo.delivery.com.deliveryusersmicroservice.Entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.Repositories.RoleRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.Repositories.UserRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.Entities.Role;
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

    public User updateUser(Long userId, UserDTO updatedUser) {
        if(!userRepository.existsById(userId)) return null;

        User existingUser = userRepository.findById(userId).get();

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setEmail(updatedUser.getEmail());

        Role role = roleRepository.findByName(updatedUser.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found."));

        existingUser.setRole(role);

        return userRepository.save(existingUser);
    }

    public boolean deleteUser(Long userId) {
        if(!userRepository.existsById(userId)) return false;

        userRepository.deleteById(userId);

        return true;
    }

    public User registerUser(UserDTO registrationDTO) {
        if(userRepository.existsByUsername(registrationDTO.getUsername()) && userRepository.existsByEmail(registrationDTO.getEmail())) {
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
