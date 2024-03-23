package kyrylo.delivery.com.deliveryusersmicroservice.dto;


public record RegisterRequest(String username, String password, String email, String roleName) {
}

