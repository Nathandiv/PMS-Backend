package pmsBackend.pmsBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pmsBackend.pmsBackend.dto.LoginRequest;
import pmsBackend.pmsBackend.dto.RegisterRequest;
import pmsBackend.pmsBackend.dto.UserResponseDTO;
import pmsBackend.pmsBackend.entity.Role;
import pmsBackend.pmsBackend.entity.User;
import pmsBackend.pmsBackend.security.JwtService;
import pmsBackend.pmsBackend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Handles registration, login, and user management")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;



    @Operation(summary = "Register new user", description = "Registers a new user with role CLIENT by default")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or duplicate user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized – invalid credentials or auth token"),
            @ApiResponse(responseCode = "404", description = "Resource not found – endpoint or related data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public User register(@Valid @RequestBody(description = "User registration payload", required = true)
                         @org.springframework.web.bind.annotation.RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }





    @Operation(summary = "Login user", description = "Logs in user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid login request or missing fields"),
            @ApiResponse(responseCode = "401", description = "Unauthorized – incorrect email or password"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public String login(@Valid @RequestBody(description = "User login credentials", required = true)
                        @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        var user = userService.authenticateUser(request);
        return jwtService.generateToken(user.getEmail());
    }





    @Operation(summary = "Get all users", description = "Returns a list of all registered users (ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request – invalid parameters"),
            @ApiResponse(responseCode = "404", description = "User(s) not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<User> allUsers() {
        return userService.getAllUsers();
    }




    @Operation(
            summary = "Get current user's profile",
            description = "Returns the authenticated user's profile information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile loaded successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized – Invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR', 'CLIENT')") // optional: remove if all authenticated users allowed
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email); // should throw if not found

        UserResponseDTO response = new UserResponseDTO(user);
        return ResponseEntity.ok(response);
    }





    @Operation(summary = "Update user", description = "Updates a user's information (ADMIN or FACILITATOR)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request – invalid data or ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR')")
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id,
                           @Valid
                           @RequestBody(description = "Updated user data", required = true)
                           @org.springframework.web.bind.annotation.RequestBody RegisterRequest request) {
        return userService.updateUser(id, request);
    }






    @Operation(summary = "Delete user", description = "Deletes a user (ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request – invalid ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }






    @Operation(summary = "Change user role", description = "Changes a user's role (ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request – invalid role or user ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{id}/role")
    public User changeRole(@PathVariable Long id,
                           @RequestBody(description = "New role", required = true)
                           @org.springframework.web.bind.annotation.RequestBody Role role) {
        return userService.changeUserRole(id, role);
    }
}
