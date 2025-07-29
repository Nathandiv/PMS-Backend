package pmsBackend.pmsBackend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import pmsBackend.pmsBackend.entity.Role;
import pmsBackend.pmsBackend.entity.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String cellphone;
    private String role;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.cellphone = user.getCellphone();
        this.role = user.getRole().name();
    }
}