package pmsBackend.pmsBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CohortRequestDTO {
    private String cohortName;
    private String duration;
    private LocalDate startDate;
    private Long facilitatorId; // ID of the User who will be the facilitator
    private String imageUrl;
    // Note: createdByUserId is handled by the service based on the authenticated user.
}