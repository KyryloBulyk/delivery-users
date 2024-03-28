package kyrylo.delivery.com.deliveryusersmicroservice.contollers;

import jakarta.validation.Valid;
import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/roles")
public class RoleController {

    private RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping()
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{roleId}")
    public Role getRoleById(@PathVariable Long roleId) {
        return roleService.getRoleById(roleId);
    }

    @PostMapping()
    public ResponseEntity<?> createNewRole(@Valid @RequestBody Role newRole) {
        Role createdRole = roleService.createNewRole(newRole);
        return ResponseEntity.status(201).body(createdRole);
    }

    @PutMapping("/{roleId}")
    public Role updateRole(@PathVariable Long roleId, @Valid @RequestBody Role updatingRole) {
        return roleService.updateRole(roleId, updatingRole);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok().body("Role was successfully deleted");
    }
}
