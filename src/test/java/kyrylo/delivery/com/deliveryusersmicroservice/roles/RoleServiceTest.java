package kyrylo.delivery.com.deliveryusersmicroservice.roles;

import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
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

    @Test
    void createNewRole_WhenRoleExists_ReturnsNull() {
        Role existingRole = new Role(1L, "ROLE_EXISTING");
        when(roleRepository.existsByName("ROLE_EXISTING")).thenReturn(true);

        Role result = roleService.createNewRole(existingRole);

        assertNull(result);
        verify(roleRepository, never()).save(any(Role.class));
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
        ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleArgumentCaptor.capture());
        Role capturedRole = roleArgumentCaptor.getValue();
        assertEquals("ROLE_UPDATED", capturedRole.getName());
    }

    @Test
    void updateRole_WhenRoleDoesNotExist_ReturnsNull() {
        Role updatedRoleInfo = new Role(99L, "ROLE_UPDATED");
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        Role result = roleService.updateRole(99L, updatedRoleInfo);

        assertNull(result);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void deleteRole_WhenRoleExists_ReturnsTrue() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(roleRepository).deleteById(1L);

        boolean result = roleService.deleteRole(1L);

        assertTrue(result);
        verify(roleRepository).deleteById(1L);
    }

    @Test
    void deleteRole_WhenRoleDoesNotExist_ReturnsFalse() {
        when(roleRepository.existsById(99L)).thenReturn(false);

        boolean result = roleService.deleteRole(99L);

        assertFalse(result);
        verify(roleRepository, never()).deleteById(99L);
    }

}
