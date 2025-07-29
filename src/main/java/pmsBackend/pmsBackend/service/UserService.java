package pmsBackend.pmsBackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pmsBackend.pmsBackend.dto.LoginRequest;
import pmsBackend.pmsBackend.dto.RegisterRequest;
import pmsBackend.pmsBackend.dto.UserResponseDTO;
import pmsBackend.pmsBackend.entity.Role;
import pmsBackend.pmsBackend.entity.User;
import pmsBackend.pmsBackend.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("A user with this email already exists");
        }

        if (userRepository.existsByCellphone(request.cellphone())) {
            throw new IllegalArgumentException("A user with this cellphone already exists");
        }

        var user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .cellphone(request.cellphone())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();
        return userRepository.save(user);
    }

    public User authenticateUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + request.email() + " not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public Optional<UserResponseDTO> getAuthenticatedUserProfile(Authentication authentication) {
        String email = authentication.getName(); // This should return the user's email/username
        Optional<User> userOptional = userRepository.findByEmail(email);

        return userOptional.map(user -> new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCellphone(),
                user.getRole().name()
        ));
    }

    public User updateUser(Long id, RegisterRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));

        userRepository.findByEmail(req.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email already in use by another user");
                });

        userRepository.findByCellphone(req.cellphone())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Cellphone already in use by another user");
                });

        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setCellphone(req.cellphone());
        user.setRole(req.role());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User changeUserRole(Long id, Role newRole) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        return userRepository.save(user);
    }


}