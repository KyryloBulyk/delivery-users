package kyrylo.delivery.com.deliveryusersmicroservice.Repositories;

import kyrylo.delivery.com.deliveryusersmicroservice.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String roleName);

    boolean existsByName(String name);
}
