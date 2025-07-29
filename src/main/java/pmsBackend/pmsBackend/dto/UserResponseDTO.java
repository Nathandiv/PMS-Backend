package pmsBackend.pmsBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pmsBackend.pmsBackend.entity.Role; // Make sure this import is correct for your Role enum
import pmsBackend.pmsBackend.entity.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
// NOTE: You might want to add @NoArgsConstructor if you plan to deserialize JSON into this DTO directly
// without using the builder or the User constructor for all cases.
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String cellphone;
    private String role; // This is a String, which requires conversion from enum

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.cellphone = user.getCellphone();
        this.role = user.getRole().name(); // Correctly converts enum to String
    }
}