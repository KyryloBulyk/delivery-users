package kyrylo.delivery.com.deliveryusersmicroservice.services;

import kyrylo.delivery.com.deliveryusersmicroservice.entities.RefreshToken;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RefreshTokenRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByUsername(String username) {
        return refreshTokenRepository.findByUser_Username(username);
    }

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByUsername(username).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(1000 * 604800))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signing request");
        }
    }

    public RefreshToken updateRefreshToken(RefreshToken refreshToken) {
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(1000 * 604800));
        return refreshTokenRepository.save(refreshToken);
    }

    public void deleteByUsername(String username) {
        refreshTokenRepository.findByUser_Username(username).ifPresent(refreshToken -> {
            refreshTokenRepository.delete(refreshToken);
        });
    }

}
