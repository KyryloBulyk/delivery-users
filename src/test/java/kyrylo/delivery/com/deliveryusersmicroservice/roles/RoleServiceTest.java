package kyrylo.delivery.com.deliveryusersmicroservice.roles;

import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.RoleAlreadyExistsException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.RoleNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RoleRepository;
import kyrylo.delivery.com.deliveryusersmicroservice.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

        Role foundRole = roleService.getRoleById(1L);

        assertNotNull(foundRole);
        assertEquals("ROLE_USER", foundRole.getName());
    }

    @Test
    void getRoleById_WhenNotFound_ThrowsRoleNotFoundException() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RoleNotFoundException.class, () -> {
            roleService.getRoleById(99L);
        });

        assertTrue(exception.getMessage().contains("was not found"));
    }

    @Test
    void createNewRole_ThrowsRoleAlreadyExistsException_WhenRoleExists() {
        Role newRole = new Role(null, "ROLE_EXISTING");
        when(roleRepository.existsByName("ROLE_EXISTING")).thenReturn(true);

        assertThrows(RoleAlreadyExistsException.class, () -> {
            roleService.createNewRole(newRole);
        });
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

    @Test
    void updateRole_WhenRoleExists_UpdatesRole() {
        Role existingRole = new Role(1L, "ROLE_OLD");
        Role updatedRoleInfo = new Role(1L, "ROLE_UPDATED");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRoleInfo);

        Role result = roleService.updateRole(1L, updatedRoleInfo);

        assertNotNull(result);
        assertEquals("ROLE_UPDATED", result.getName());
    }

    @Test
    void updateRole_WhenRoleDoesNotExist_ThrowsRoleNotFoundException() {
        Role updatedRoleInfo = new Role(99L, "ROLE_UPDATED");
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> {
            roleService.updateRole(99L, updatedRoleInfo);
        });
    }

    @Test
    void deleteRole_WhenRoleExists_ReturnsTrue() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(roleRepository).deleteById(1L);

        roleService.deleteRole(1L);

        verify(roleRepository).deleteById(1L);
    }

    @Test
    void deleteRole_WhenRoleDoesNotExist_ThrowsRoleNotFoundException() {
        when(roleRepository.existsById(99L)).thenReturn(false);

        assertThrows(RoleNotFoundException.class, () -> {
            roleService.deleteRole(99L);
        });
    }

}
