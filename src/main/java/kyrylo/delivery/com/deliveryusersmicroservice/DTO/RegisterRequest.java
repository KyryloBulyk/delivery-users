package kyrylo.delivery.com.deliveryusersmicroservice.DTO;


public record RegisterRequest(String username, String password, String email, String roleName) {
}

