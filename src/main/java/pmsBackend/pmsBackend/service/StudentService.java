package pmsBackend.pmsBackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pmsBackend.pmsBackend.dto.CohortResponseDTO;
import pmsBackend.pmsBackend.dto.StudentRequestDTO;
import pmsBackend.pmsBackend.dto.StudentResponseDTO;
import pmsBackend.pmsBackend.dto.UserResponseDTO;
import pmsBackend.pmsBackend.entity.Cohort;
import pmsBackend.pmsBackend.entity.Student;
import pmsBackend.pmsBackend.entity.User;
import pmsBackend.pmsBackend.repository.CohortRepository;
import pmsBackend.pmsBackend.repository.StudentRepository;
import pmsBackend.pmsBackend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CohortRepository cohortRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, UserRepository userRepository, CohortRepository cohortRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.cohortRepository = cohortRepository;
    }


    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO, Long createdByUserId) {
        // 1. Fetch related entities
        User creator = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new EntityNotFoundException("Creator user not found with ID: " + createdByUserId));

        Cohort cohort = cohortRepository.findById(studentRequestDTO.getCohortId())
                .orElseThrow(() -> new EntityNotFoundException("Cohort not found with ID: " + studentRequestDTO.getCohortId()));

        // 2. Build the Student entity from DTO
        Student student = Student.builder()
                .fullNames(studentRequestDTO.getFullNames())
                .emailAddress(studentRequestDTO.getEmailAddress())
                .phoneNumber(studentRequestDTO.getPhoneNumber())
                .dateOfBirth(studentRequestDTO.getDateOfBirth())
                .homeAddress(studentRequestDTO.getHomeAddress())
                .cohort(cohort) // Set the actual Cohort entity
                .qualification(studentRequestDTO.getQualification())
                .progress(studentRequestDTO.getProgress())
                .status(studentRequestDTO.getStatus())
                .createdBy(creator) // Set the actual User entity as creator
                .createdAt(LocalDateTime.now()) // Set creation timestamp (though @CreationTimestamp handles it)
                .build();

        // 3. Save the student entity
        Student savedStudent = studentRepository.save(student);

        // 4. Convert and return Response DTO
        return convertToStudentResponseDTO(savedStudent);
    }


    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + id));
        return convertToStudentResponseDTO(student);
    }


    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToStudentResponseDTO)
                .collect(Collectors.toList());
    }



    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO studentRequestDTO, Long updatedByUserId) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + id));

        // Update basic fields
        existingStudent.setFullNames(studentRequestDTO.getFullNames());
        existingStudent.setEmailAddress(studentRequestDTO.getEmailAddress());
        existingStudent.setPhoneNumber(studentRequestDTO.getPhoneNumber());
        existingStudent.setDateOfBirth(studentRequestDTO.getDateOfBirth());
        existingStudent.setHomeAddress(studentRequestDTO.getHomeAddress());
        existingStudent.setQualification(studentRequestDTO.getQualification());
        existingStudent.setProgress(studentRequestDTO.getProgress());
        existingStudent.setStatus(studentRequestDTO.getStatus());
        // lastUpdated will be automatically managed by @UpdateTimestamp

        // Update Cohort if changed
        if (!existingStudent.getCohort().getId().equals(studentRequestDTO.getCohortId())) {
            Cohort newCohort = cohortRepository.findById(studentRequestDTO.getCohortId())
                    .orElseThrow(() -> new EntityNotFoundException("New Cohort not found with ID: " + studentRequestDTO.getCohortId()));
            existingStudent.setCohort(newCohort);
        }

        // Save the updated student
        Student updatedStudent = studentRepository.save(existingStudent);
        return convertToStudentResponseDTO(updatedStudent);
    }


    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
    }


    private StudentResponseDTO convertToStudentResponseDTO(Student student) {
        // Convert Cohort entity to CohortResponseDTO
        CohortResponseDTO cohortDTO = null;
        if (student.getCohort() != null) {
            cohortDTO = CohortResponseDTO.builder()
                    .id(student.getCohort().getId())
                    .cohortName(student.getCohort().getCohortName())
                    .duration(student.getCohort().getDuration())
                    .startDate(student.getCohort().getStartDate())
                    .imageUrl(student.getCohort().getImageUrl())
                    // Nested facilitator in CohortResponseDTO
                    .facilitator(student.getCohort().getFacilitator() != null ?
                            UserResponseDTO.builder()
                                    .id(student.getCohort().getFacilitator().getId())
                                    .fullName(student.getCohort().getFacilitator().getFullName())
                                    .email(student.getCohort().getFacilitator().getEmail())
                                    .role(student.getCohort().getFacilitator().getRole().name()) // <--- .name() for Role enum
                                    .build() : null)
                    .build();
        }

        // Convert CreatedBy User entity to UserResponseDTO
        UserResponseDTO createdByDTO = null;
        if (student.getCreatedBy() != null) {
            createdByDTO = UserResponseDTO.builder()
                    .id(student.getCreatedBy().getId())
                    .fullName(student.getCreatedBy().getFullName())
                    .email(student.getCreatedBy().getEmail())
                    .role(student.getCreatedBy().getRole().name()) // <--- .name() for Role enum
                    .build();
        }

        return StudentResponseDTO.builder()
                .id(student.getId())
                .fullNames(student.getFullNames())
                .emailAddress(student.getEmailAddress())
                .phoneNumber(student.getPhoneNumber())
                .dateOfBirth(student.getDateOfBirth())
                .homeAddress(student.getHomeAddress())
                .cohort(cohortDTO)
                .qualification(student.getQualification())
                .progress(student.getProgress())
                .status(student.getStatus())
                .createdBy(createdByDTO)
                .createdAt(student.getCreatedAt())
                .lastUpdated(student.getLastUpdated())
                .build();
    }
}