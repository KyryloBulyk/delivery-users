package kyrylo.delivery.com.deliveryusersmicroservice.exceptions.roleExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class RoleAlreadyExistsException extends RuntimeException{
    public RoleAlreadyExistsException(String name) {
        super("Role with name " + name + " already exists.");
    }
}
