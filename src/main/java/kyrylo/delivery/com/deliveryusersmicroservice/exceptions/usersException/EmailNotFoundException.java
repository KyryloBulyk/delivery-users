package kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EmailNotFoundException extends RuntimeException{
    public EmailNotFoundException(String email) {
        super("Email " + email + " was not found.");
    }
}
