package pmsBackend.pmsBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pmsBackend.pmsBackend.entity.StudentStatus; // Import StudentStatus enum

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequestDTO {
    private String fullNames;
    private String emailAddress;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String homeAddress;
    private Long cohortId; // ID of the Cohort
    private String qualification;
    private int progress;
    private StudentStatus status;
    // Note: createdByUserId will be inferred from the authenticated user,
    // so it's not typically part of the request DTO unless explicitly sent.
    // We'll handle setting 'createdBy' in the service layer using the authenticated user's ID.
}