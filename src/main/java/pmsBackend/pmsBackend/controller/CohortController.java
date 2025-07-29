package pmsBackend.pmsBackend.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pmsBackend.pmsBackend.dto.CohortRequestDTO;
import pmsBackend.pmsBackend.dto.CohortResponseDTO;
import pmsBackend.pmsBackend.entity.User; // Import your User entity to cast AuthenticationPrincipal
import pmsBackend.pmsBackend.service.CohortService;

import java.util.List;

@RestController
@RequestMapping("/api/cohorts")
public class CohortController {

    private final CohortService cohortService;

    @Autowired
    public CohortController(CohortService cohortService) {
        this.cohortService = cohortService;
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CohortResponseDTO> createCohort(
            @RequestBody CohortRequestDTO cohortRequestDTO,
            @AuthenticationPrincipal User authenticatedUser) { // Assuming User entity is your UserDetails object
        try {
            Long createdByUserId = authenticatedUser.getId();
            CohortResponseDTO newCohort = cohortService.createCohort(cohortRequestDTO, createdByUserId);
            return new ResponseEntity<>(newCohort, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity("Error creating cohort: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR', 'STUDENT')")
    public ResponseEntity<CohortResponseDTO> getCohortById(@PathVariable Long id) {
        try {
            CohortResponseDTO cohort = cohortService.getCohortById(id);
            return ResponseEntity.ok(cohort);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity("Error retrieving cohort: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR', 'STUDENT')")
    public ResponseEntity<List<CohortResponseDTO>> getAllCohorts() {
        try {
            List<CohortResponseDTO> cohorts = cohortService.getAllCohorts();
            return ResponseEntity.ok(cohorts);
        } catch (Exception e) {
            return new ResponseEntity("Error retrieving cohorts: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CohortResponseDTO> updateCohort(
            @PathVariable Long id,
            @RequestBody CohortRequestDTO cohortRequestDTO,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            Long updatedByUserId = authenticatedUser.getId();
            CohortResponseDTO updatedCohort = cohortService.updateCohort(id, cohortRequestDTO, updatedByUserId);
            return ResponseEntity.ok(updatedCohort);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity("Error updating cohort: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCohort(@PathVariable Long id) {
        try {
            cohortService.deleteCohort(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity("Error deleting cohort: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}