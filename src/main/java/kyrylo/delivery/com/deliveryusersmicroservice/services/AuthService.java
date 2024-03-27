package kyrylo.delivery.com.deliveryusersmicroservice.services;

import kyrylo.delivery.com.deliveryusersmicroservice.dto.JwtResponse;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.RefreshToken;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.authExceptions.RegistrationException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.roleExceptions.RoleNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RoleRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthService(JwtService jwtService, UserDetailsService userDetailsService,
                       UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, RefreshTokenService refreshTokenService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
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
            throw new RegistrationException("Username or email already exists.");
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setEmail(registerRequest.email());

        Role role = roleRepository.findByName(registerRequest.roleName())
                .orElseThrow(() -> new RoleNotFoundException(registerRequest.roleName()));

        user.setRole(role);

        return userRepository.save(user);
    }

    public UserDetails loadUserByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    public JwtResponse refreshAccessToken(String bearerToken) {
        if (!bearerToken.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect Authorization header format.");
        }

        String onlyBearerToken = bearerToken.substring(7);
        String username = extractUsername(onlyBearerToken);

        RefreshToken existingRefreshToken = refreshTokenService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Refresh Token not found"));

        refreshTokenService.verifyExpiration(existingRefreshToken);

        UserDetails userDetails = loadUserByUsername(username);
        String newAccessToken = generateToken(userDetails);

        return new JwtResponse(newAccessToken);
    }
}