package kyrylo.delivery.com.deliveryusersmicroservice.roles;

import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RoleRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRoles_ReturnsRoleList() {
        Role role1 = new Role(1L, "ROLE_USER");
        Role role2 = new Role(2L, "ROLE_ADMIN");
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        List<Role> roles = roleService.getAllRoles();

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertEquals("ROLE_USER", roles.get(0).getName());
        assertEquals("ROLE_ADMIN", roles.get(1).getName());
    }

    @Test
    void getRoleById_ReturnsRole() {
        Role role = new Role(1L, "ROLE_USER");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Optional<Role> foundRole = roleService.getRoleById(1L);

        assertTrue(foundRole.isPresent());
        assertEquals("ROLE_USER", foundRole.get().getName());
    }

    @Test
    void createNewRole_ReturnsNewRole() {
        Role newRole = new Role(null, "ROLE_NEW");
        Role savedRole = new Role(3L, "ROLE_NEW");
        when(roleRepository.existsByName("ROLE_NEW")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        Role result = roleService.createNewRole(newRole);

        assertNotNull(result);
        assertEquals("ROLE_NEW", result.getName());
    }

}
