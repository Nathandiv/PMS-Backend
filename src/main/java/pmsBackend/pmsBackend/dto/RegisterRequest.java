package pmsBackend.pmsBackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pmsBackend.pmsBackend.entity.Role;

public record RegisterRequest(

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,

        @NotBlank(message = "Cellphone is required")
        String cellphone,

        @NotBlank(message = "Password is required")
        @Size(min = 2, message = "Password must be at least 2 characters long")
        String password,

        Role role
) {}