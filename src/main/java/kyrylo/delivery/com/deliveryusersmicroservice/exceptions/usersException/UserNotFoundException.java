package kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long userId) {
        super("User with ID " + userId + " was not found.");
    }
}
