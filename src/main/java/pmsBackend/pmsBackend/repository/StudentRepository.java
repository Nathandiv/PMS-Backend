package pmsBackend.pmsBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pmsBackend.pmsBackend.entity.Student; // Import your Student entity

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // You can add custom query methods here if needed, e.g., findByEmailAddress, findByCohort
}