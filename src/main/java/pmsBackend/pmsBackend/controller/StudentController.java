package pmsBackend.pmsBackend.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // To get the authenticated user object
import org.springframework.web.bind.annotation.*;
import pmsBackend.pmsBackend.dto.StudentRequestDTO;
import pmsBackend.pmsBackend.dto.StudentResponseDTO;
import pmsBackend.pmsBackend.entity.User; // Import your User entity to cast AuthenticationPrincipal
import pmsBackend.pmsBackend.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR')")
    public ResponseEntity<StudentResponseDTO> createStudent(
            @RequestBody StudentRequestDTO studentRequestDTO,
            @AuthenticationPrincipal User authenticatedUser) { // Assuming User entity is your UserDetails object
        try {
            // Get the ID of the authenticated user to pass as createdByUserId
            Long createdByUserId = authenticatedUser.getId();
            StudentResponseDTO newStudent = studentService.createStudent(studentRequestDTO, createdByUserId);
            return new ResponseEntity<>(newStudent, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity("Error creating student: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR', 'STUDENT')")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id) {
        try {
            StudentResponseDTO student = studentService.getStudentById(id);
            return ResponseEntity.ok(student);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity("Error retrieving student: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR')")
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        try {
            List<StudentResponseDTO> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return new ResponseEntity("Error retrieving students: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR')")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentRequestDTO studentRequestDTO,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            // Get the ID of the authenticated user (for auditing if needed in service)
            Long updatedByUserId = authenticatedUser.getId();
            StudentResponseDTO updatedStudent = studentService.updateStudent(id, studentRequestDTO, updatedByUserId);
            return ResponseEntity.ok(updatedStudent);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity("Error updating student: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity("Error deleting student: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}