package kyrylo.delivery.com.deliveryusersmicroservice.repositories;

import kyrylo.delivery.com.deliveryusersmicroservice.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser_Username(String username);
}
