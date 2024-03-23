package kyrylo.delivery.com.deliveryusersmicroservice.services;

import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long roleId) {
        return roleRepository.findById(roleId);
    }

    public Role createNewRole(Role newRole) {
        if(roleRepository.existsByName(newRole.getName())) return null;

        return roleRepository.save(newRole);
    }

    public Role updateRole(Long roleId, Role updatingRole) {
        Optional<Role> existingRole = roleRepository.findById(roleId);

        if(existingRole.isEmpty()) return null;

        Role updatedRole = existingRole.get();
        updatedRole.setName(updatingRole.getName());

        return roleRepository.save(updatedRole);
    }

    public boolean deleteRole(Long roleId) {
        if(!roleRepository.existsById(roleId)) return false;

        roleRepository.deleteById(roleId);

        return true;
    }
}
