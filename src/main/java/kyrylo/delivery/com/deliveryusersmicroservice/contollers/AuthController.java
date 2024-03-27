package kyrylo.delivery.com.deliveryusersmicroservice.contollers;

import kyrylo.delivery.com.deliveryusersmicroservice.dto.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.JwtResponse;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.RefreshToken;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.services.AuthService;
import kyrylo.delivery.com.deliveryusersmicroservice.services.RefreshTokenService;
import kyrylo.delivery.com.deliveryusersmicroservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;
    private AuthenticationManager authenticationManager;
    private RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }

    @PostMapping("/token")
    public JwtResponse getToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        refreshTokenService.findByUsername(authRequest.username())
                .map(refreshTokenService::updateRefreshToken)
                .orElseGet(() -> refreshTokenService.createRefreshToken(authRequest.username()));

        String accessToken = authService.generateToken(userDetails);
        return new JwtResponse(accessToken);
    }

    @PostMapping("/refresh")
    public JwtResponse refreshAccessToken(@RequestHeader("Authorization") String bearerToken) {
        return authService.refreshAccessToken(bearerToken);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestBody JwtResponse token) {
        try {
            authService.validateToken(token.accessToken());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        if (!bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incorrect Authorization header format.");
        }

        String accessToken = bearerToken.substring(7);
        String username = authService.extractUsername(accessToken);

        refreshTokenService.deleteByUsername(username);

        return ResponseEntity.ok("Logged out successfully.");
    }
}

