package kyrylo.delivery.com.deliveryusersmicroservice.contollers;

import jakarta.validation.Valid;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@RestController
@RequestMapping("api/roles")
public class RoleController {

    private RoleService roleService;
    private static final Logger logger = LogManager.getLogger(RoleController.class);

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
        logger.info("RoleController initialized");
    }

    @GetMapping()
    public ResponseEntity<List<Role>> getAllRoles() {
        logger.info("Received request to get all roles");
        List<Role> roles = roleService.getAllRoles();
        logger.info("Returning {} roles", roles.size());
        return ResponseEntity.ok(roles);
    }


    @GetMapping("/{roleId}")
    public Role getRoleById(@PathVariable Long roleId) {
        logger.info("Received request to get role by ID: {}", roleId);
        try {
            Role role = roleService.getRoleById(roleId);
            logger.info("Found role with ID: {}", roleId);
            return role;
        } catch (Exception e) {
            logger.error("Error finding role with ID: {}", roleId, e);
            throw e;
        }
    }

    @PostMapping()
    public ResponseEntity<Role> createNewRole(@Valid @RequestBody Role newRole) {
        logger.info("Received request to create new role: {}", newRole.getName());
        try {
            Role createdRole = roleService.createNewRole(newRole);
            logger.info("Role created successfully: {}", newRole.getName());
            return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating new role: {}", newRole.getName(), e);
            throw e;
        }
    }

    @PutMapping("/{roleId}")
    public Role updateRole(@PathVariable Long roleId, @Valid @RequestBody Role updatingRole) {
        logger.info("Received request to update role with ID: {}", roleId);
        try {
            Role updatedRole = roleService.updateRole(roleId, updatingRole);
            logger.info("Role with ID: {} updated successfully", roleId);
            return updatedRole;
        } catch (Exception e) {
            logger.error("Error updating role with ID: {}", roleId, e);
            throw e;
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Long roleId) {
        logger.info("Received request to delete role with ID: {}", roleId);
        try {
            roleService.deleteRole(roleId);
            logger.info("Role with ID: {} deleted successfully", roleId);
            return ResponseEntity.ok("Role was successfully deleted");
        } catch (Exception e) {
            logger.error("Error deleting role with ID: {}", roleId, e);
            throw e;
        }
    }
}
