package kyrylo.delivery.com.deliveryusersmicroservice.repositories;

import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String roleName);

    boolean existsByName(String name);
}
