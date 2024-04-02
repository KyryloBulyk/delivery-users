package kyrylo.delivery.com.deliveryusersmicroservice.services;

import kyrylo.delivery.com.deliveryusersmicroservice.entities.Role;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.roleExceptions.RoleAlreadyExistsException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.roleExceptions.RoleNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.repositories.RoleRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private RoleRepository roleRepository;
    private static final Logger logger = LogManager.getLogger(RoleService.class);

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        logger.info("RoleService initialized");
    }


    public List<Role> getAllRoles() {
        logger.info("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        logger.info("Found {} roles", roles.size());
        return roles;
    }

    public Role getRoleById(Long roleId) {
        logger.info("Fetching role with ID: {}", roleId);
        return roleRepository.findById(roleId).orElseThrow(() -> {
            logger.error("Role not found with ID: {}", roleId);
            return new RoleNotFoundException(roleId);
        });
    }

    public Role createNewRole(Role newRole) {
        logger.info("Creating new role: {}", newRole.getName());
        if(roleRepository.existsByName(newRole.getName())) {
            logger.error("Role already exists: {}", newRole.getName());
            throw new RoleAlreadyExistsException(newRole.getName());
        }

        Role createdRole = roleRepository.save(newRole);
        logger.info("Role created successfully: {}", createdRole.getName());
        return createdRole;
    }

    public Role updateRole(Long roleId, Role updatingRole) {
        logger.info("Updating role with ID: {}", roleId);
        Role existingRole = roleRepository.findById(roleId).orElseThrow(() -> {
            logger.error("Role not found with ID: {}", roleId);
            return new RoleNotFoundException(roleId);
        });

        existingRole.setName(updatingRole.getName());
        Role updatedRole = roleRepository.save(existingRole);
        logger.info("Role with ID: {} updated successfully to {}", roleId, updatingRole.getName());
        return updatedRole;
    }

    public void deleteRole(Long roleId) {
        logger.info("Deleting role with ID: {}", roleId);
        if(!roleRepository.existsById(roleId)) {
            logger.error("Role not found with ID: {}", roleId);
            throw new RoleNotFoundException(roleId);
        }

        roleRepository.deleteById(roleId);
        logger.info("Role with ID: {} deleted successfully", roleId);
    }
}
