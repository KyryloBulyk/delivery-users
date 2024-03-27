package kyrylo.delivery.com.deliveryusersmicroservice.services;

import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.RoleAlreadyExistsException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.RoleNotFoundException;
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

    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));
    }

    public Role createNewRole(Role newRole) {
        if(roleRepository.existsByName(newRole.getName()))
            throw new RoleAlreadyExistsException(newRole.getName());

        return roleRepository.save(newRole);
    }

    public Role updateRole(Long roleId, Role updatingRole) {
        Role existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        existingRole.setName(updatingRole.getName());

        return roleRepository.save(existingRole);
    }

    public void deleteRole(Long roleId) {
        if(!roleRepository.existsById(roleId))
            throw new RoleNotFoundException(roleId);

        roleRepository.deleteById(roleId);
    }
}
