package kyrylo.delivery.com.deliveryusersmicroservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, message = "Username must be at least 3 characters long")
        String username,

        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Role name is required")
        String roleName) {
}

