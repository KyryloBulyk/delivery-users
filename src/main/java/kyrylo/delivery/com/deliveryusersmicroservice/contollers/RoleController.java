package kyrylo.delivery.com.deliveryusersmicroservice.contollers;

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
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        return roleService.getRoleById(roleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<?> createNewRole(@RequestBody Role newRole) {
        Role role = roleService.createNewRole(newRole);

        if(role == null) {
            return new ResponseEntity<>("Creating is failed", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(role, HttpStatus.CREATED);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<?> updateRole(@PathVariable Long roleId, @RequestBody Role updatingRole) {
        Role role = roleService.updateRole(roleId, updatingRole);

        if(role == null) {
            return new ResponseEntity<>("Role wasn't found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Long roleId) {
        boolean deletingRole = roleService.deleteRole(roleId);

        if(!deletingRole) {
            return new ResponseEntity<>("Role wasn't found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Role was successfully deleted", HttpStatus.OK);
    }
}
