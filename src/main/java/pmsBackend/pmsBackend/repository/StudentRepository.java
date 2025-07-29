package pmsBackend.pmsBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pmsBackend.pmsBackend.entity.Student; // Import your Student entity

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmailAddress(String emailAddress);
    List<Student> findByFullNames(String fullNames);
}