package kyrylo.delivery.com.deliveryusersmicroservice.exceptions.roleExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Long id) {
        super("Role with ID " + id + " was not found.");
    }

    public RoleNotFoundException(String roleName) {
        super("Role with name " + roleName + " was not found.");
    }
}
