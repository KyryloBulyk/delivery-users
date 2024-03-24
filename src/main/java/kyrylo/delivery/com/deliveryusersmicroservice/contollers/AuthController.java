package kyrylo.delivery.com.deliveryusersmicroservice.contollers;

import kyrylo.delivery.com.deliveryusersmicroservice.dto.AuthRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.JwtResponse;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RefreshTokenRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.dto.RegisterRequest;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.RefreshToken;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.User;
import kyrylo.delivery.com.deliveryusersmicroservice.filter.JwtAuthFilter;
import kyrylo.delivery.com.deliveryusersmicroservice.services.AuthService;
import kyrylo.delivery.com.deliveryusersmicroservice.services.RefreshTokenService;
import kyrylo.delivery.com.deliveryusersmicroservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.username());
        String token = authService.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token, refreshToken.getToken()));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenRequest.refreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshTokenService.verifyExpiration(refreshToken);

        UserDetails userDetails = userService.loadUserByUsername(refreshToken.getUser().getUsername());
        String accessToken = authService.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken.getToken()));
    }



}

