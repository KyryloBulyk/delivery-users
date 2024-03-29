package kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String username) {
        super("User with name " + username + " already exists.");
    }
}
