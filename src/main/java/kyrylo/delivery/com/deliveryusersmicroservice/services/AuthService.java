package kyrylo.delivery.com.deliveryusersmicroservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private JwtService jwtService;

    @Autowired
    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String generateToken(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }
}