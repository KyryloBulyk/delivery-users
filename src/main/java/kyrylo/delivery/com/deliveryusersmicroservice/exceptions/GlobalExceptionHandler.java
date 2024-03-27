package kyrylo.delivery.com.deliveryusersmicroservice.exceptions;

import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.authExceptions.InvalidTokenException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.authExceptions.RegistrationException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.roleExceptions.RoleAlreadyExistsException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.roleExceptions.RoleNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException.EmailNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.rmi.AlreadyBoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<?> roleNotFoundExceptionHandler(RoleNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyBoundException.class)
    public ResponseEntity<?> roleAlreadyExistsExceptionHandler(RoleAlreadyExistsException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.CONTINUE.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFoundExceptionHandler(UserNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<?> emailNotFoundExceptionHandler(EmailNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<?> registrationExceptionHandler(RegistrationException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
}
