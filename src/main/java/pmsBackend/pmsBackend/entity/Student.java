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
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullNames;

    @Column(unique = true, nullable = false)
    private String emailAddress;

    private String phoneNumber;

    private LocalDate dateOfBirth;
    private String homeAddress;

    // Reference to the Cohort entity
    @ManyToOne(fetch = FetchType.LAZY) // Many students can belong to one cohort
    @JoinColumn(name = "cohort_id", referencedColumnName = "id", nullable = false)
    private Cohort cohort; // This now references the Cohort entity

    private String qualification;
    private int progress;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    // --- Tracking who created the Student record ---
    @ManyToOne(fetch = FetchType.LAZY) // Many students can be created by one user (Admin/Facilitator)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id", nullable = false) // Foreign key to User table
    private User createdBy;

    // --- Timestamps ---
    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}