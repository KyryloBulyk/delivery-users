package kyrylo.delivery.com.deliveryusersmicroservice.exceptions.authExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Error during registration")
public class RegistrationException extends RuntimeException{
    public RegistrationException(String message) {
        super(message);
    }
}
