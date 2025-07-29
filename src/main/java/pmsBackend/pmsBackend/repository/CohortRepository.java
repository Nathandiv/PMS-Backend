package pmsBackend.pmsBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pmsBackend.pmsBackend.entity.Cohort; // Import your Cohort entity

@Repository
public interface CohortRepository extends JpaRepository<Cohort, Long> {
    // You can add custom query methods here if needed, e.g., findByCohortName
}