package pmsBackend.pmsBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cohorts")
public class Cohort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cohortName; // e.g., "Sudan", "Spring 2024"

    private String duration; // e.g., "3 months", "6 weeks"

    private LocalDate startDate; // Use LocalDate for dates

    // Reference to a User who is the Facilitator for this cohort
    @ManyToOne(fetch = FetchType.LAZY) // Many cohorts can have one facilitator (User)
    @JoinColumn(name = "facilitator_user_id", referencedColumnName = "id", nullable = false)
    private User facilitator;

    @Column(length = 500) // Assuming image URL or path
    private String imageUrl; // For "image of cohort"

    // --- Tracking who created the Cohort record ---
    @ManyToOne(fetch = FetchType.LAZY) // Many cohorts can be created by one user (Admin)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id", nullable = false) // Foreign key to User table
    private User createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}