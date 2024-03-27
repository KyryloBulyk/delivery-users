package kyrylo.delivery.com.deliveryusersmicroservice.services;

import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RoleRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;

    @Autowired
    public AuthService(JwtService jwtService, UserDetailsService userDetailsService,
                            UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public String generateToken(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }

    public String extractUsername(String token) {
        return jwtService.extractUsernameWithoutValidation(token);
    }

    public void validateToken(String token) throws Exception {
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtService.extractUsername(token));
        jwtService.validateToken(token, userDetails);
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

    public UserDetails loadUserByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

}