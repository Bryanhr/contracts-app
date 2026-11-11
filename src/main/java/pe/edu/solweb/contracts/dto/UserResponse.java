package pe.edu.solweb.contracts.dto;

import pe.edu.solweb.contracts.entity.User;

public record UserResponse(Long id, String name, String email, String role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
