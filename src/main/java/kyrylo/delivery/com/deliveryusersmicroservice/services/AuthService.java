package kyrylo.delivery.com.deliveryusersmicroservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private JwtService jwtService;

    private UserDetailsService userDetailsService;

    @Autowired
    public AuthService(JwtService jwtService, UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
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

}