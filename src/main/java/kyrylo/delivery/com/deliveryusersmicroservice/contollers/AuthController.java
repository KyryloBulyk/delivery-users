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

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserService userService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest);
        if(user == null) {
            return new ResponseEntity<>("Registration failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Optional<RefreshToken> existingRefreshToken = refreshTokenService.findByUsername(authRequest.username());

        RefreshToken refreshToken;
        if (existingRefreshToken.isPresent()) {
            refreshToken = refreshTokenService.updateRefreshToken(existingRefreshToken.get());
        } else {
            refreshToken = refreshTokenService.createRefreshToken(authRequest.username());
        }

        if(refreshToken == null) {
            throw new RuntimeException("Error in processing Refresh Token");
        }

        String accessToken = authService.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(accessToken));
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String bearerToken) {
        if (!bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incorrect Authorization header format.");
        }

        String onlyBearerToken = bearerToken.substring(7);
        String username = authService.extractUsername(onlyBearerToken);

        RefreshToken existingRefreshToken = refreshTokenService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Refresh Token not found"));

        refreshTokenService.verifyExpiration(existingRefreshToken);

        UserDetails userDetails = userService.loadUserByUsername(username);
        String newAccessToken = authService.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(newAccessToken));
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

