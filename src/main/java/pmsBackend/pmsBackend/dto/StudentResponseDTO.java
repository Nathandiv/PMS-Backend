package pmsBackend.pmsBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pmsBackend.pmsBackend.entity.StudentStatus; // Import StudentStatus enum

import java.time.LocalDate;
import java.time.LocalDateTime; // Import LocalDateTime for timestamps

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDTO {
    private Long id;
    private String fullNames;
    private String emailAddress;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String homeAddress;
    private CohortResponseDTO cohort; // Nested DTO for the Cohort
    private String qualification;
    private int progress;
    private StudentStatus status;
    private UserResponseDTO createdBy; // <--- ADDED THIS FIELD
    private LocalDateTime createdAt;   // <--- ADDED THIS FIELD
    private LocalDateTime lastUpdated; // <--- ADDED THIS FIELD
}