package kyrylo.delivery.com.deliveryusersmicroservice.exceptions;

import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.authExceptions.InvalidTokenException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.authExceptions.RegistrationException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.roleExceptions.RoleAlreadyExistsException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.roleExceptions.RoleNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException.EmailNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException.UserNotFoundException;
import kyrylo.delivery.com.deliveryusersmicroservice.exceptions.usersException.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.rmi.AlreadyBoundException;
import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<?> userAlreadyExistsExceptionHandler(UsernameAlreadyExistsException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.CONFLICT.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
