package pmsBackend.pmsBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime; // Import for LocalDateTime

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CohortResponseDTO {
    private Long id;
    private String cohortName;
    private String duration;
    private LocalDate startDate;
    private String imageUrl;
    private UserResponseDTO facilitator; // Nested DTO for the facilitator
    private UserResponseDTO createdBy;   // <--- ADDED THIS FIELD
    private LocalDateTime createdAt;     // <--- ADDED THIS FIELD
    private LocalDateTime updatedAt;     // <--- ADDED THIS FIELD (Corresponds to Cohort entity's updatedAt)
}