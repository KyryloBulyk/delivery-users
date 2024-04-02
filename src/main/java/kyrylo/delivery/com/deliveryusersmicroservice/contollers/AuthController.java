package kyrylo.delivery.com.deliveryusersmicroservice.contollers;

import jakarta.validation.Valid;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.JwtResponse;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.authExceptions.InvalidTokenException;
import kyrylo.delivery.com.deliveryusersmicroservice.services.AuthService;
import kyrylo.delivery.com.deliveryusersmicroservice.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        logger.info("AuthController initialized");
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Registering new user: {}", registerRequest.username());
        User user = authService.registerUser(registerRequest);
        logger.info("User registered successfully: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }


    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getToken(@RequestBody AuthRequest authRequest) {
        logger.info("Authenticating user: {}", authRequest.username());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        refreshTokenService.findByUsername(authRequest.username())
                .map(refreshTokenService::updateRefreshToken)
                .orElseGet(() -> refreshTokenService.createRefreshToken(authRequest.username()));

        String accessToken = authService.generateToken(userDetails);
        logger.info("Token generated for user: {}", authRequest.username());
        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshAccessToken(@RequestHeader("Authorization") String bearerToken) {
        logger.info("Refreshing access token");
        JwtResponse jwtResponse = authService.refreshAccessToken(bearerToken);
        logger.info("Access token refreshed");
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestBody JwtResponse token) {
        try {
            logger.info("Validating token");
            authService.validateToken(token.accessToken());
            logger.info("Token validated successfully");
            return ResponseEntity.ok().build();
        } catch (InvalidTokenException e) {
            logger.error("Token validation failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        logger.info("Logging out user");
        try {
            authService.logout(bearerToken);
            logger.info("User logged out successfully");
            return ResponseEntity.ok("Logged out successfully.");
        } catch (ResponseStatusException e) {
            logger.error("Logout failed", e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}

