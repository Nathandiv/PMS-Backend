package pmsBackend.pmsBackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pmsBackend.pmsBackend.dto.CohortRequestDTO;
import pmsBackend.pmsBackend.dto.CohortResponseDTO;
import pmsBackend.pmsBackend.dto.UserResponseDTO;
import pmsBackend.pmsBackend.entity.Cohort;
import pmsBackend.pmsBackend.entity.User;
import pmsBackend.pmsBackend.repository.CohortRepository;
import pmsBackend.pmsBackend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CohortService {

    private final CohortRepository cohortRepository;
    private final UserRepository userRepository;

    @Autowired
    public CohortService(CohortRepository cohortRepository, UserRepository userRepository) {
        this.cohortRepository = cohortRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public CohortResponseDTO createCohort(CohortRequestDTO cohortRequestDTO, Long createdByUserId) {
        // 1. Fetch related entities
        User facilitator = userRepository.findById(cohortRequestDTO.getFacilitatorId())
                .orElseThrow(() -> new EntityNotFoundException("Facilitator user not found with ID: " + cohortRequestDTO.getFacilitatorId()));

        User creator = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new EntityNotFoundException("Creator user not found with ID: " + createdByUserId));

        // 2. Build the Cohort entity from DTO
        Cohort cohort = Cohort.builder()
                .cohortName(cohortRequestDTO.getCohortName())
                .duration(cohortRequestDTO.getDuration())
                .startDate(cohortRequestDTO.getStartDate())
                .imageUrl(cohortRequestDTO.getImageUrl())
                .facilitator(facilitator) // Set the actual User entity as facilitator
                .createdBy(creator)       // Set the actual User entity as creator
                .createdAt(LocalDateTime.now()) // Set creation timestamp (though @CreationTimestamp handles it)
                .build();

        // 3. Save the cohort entity
        Cohort savedCohort = cohortRepository.save(cohort);

        // 4. Convert and return Response DTO
        return convertToCohortResponseDTO(savedCohort);
    }


    @Transactional(readOnly = true)
    public CohortResponseDTO getCohortById(Long id) {
        Cohort cohort = cohortRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cohort not found with ID: " + id));
        return convertToCohortResponseDTO(cohort);
    }


    @Transactional(readOnly = true)
    public List<CohortResponseDTO> getAllCohorts() {
        return cohortRepository.findAll().stream()
                .map(this::convertToCohortResponseDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public CohortResponseDTO updateCohort(Long id, CohortRequestDTO cohortRequestDTO, Long updatedByUserId) {
        Cohort existingCohort = cohortRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cohort not found with ID: " + id));

        // Update basic fields
        existingCohort.setCohortName(cohortRequestDTO.getCohortName());
        existingCohort.setDuration(cohortRequestDTO.getDuration());
        existingCohort.setStartDate(cohortRequestDTO.getStartDate());
        existingCohort.setImageUrl(cohortRequestDTO.getImageUrl());
        // lastUpdated will be automatically managed by @UpdateTimestamp

        // Update Facilitator if changed
        if (!existingCohort.getFacilitator().getId().equals(cohortRequestDTO.getFacilitatorId())) {
            User newFacilitator = userRepository.findById(cohortRequestDTO.getFacilitatorId())
                    .orElseThrow(() -> new EntityNotFoundException("New Facilitator user not found with ID: " + cohortRequestDTO.getFacilitatorId()));
            existingCohort.setFacilitator(newFacilitator);
        }

        // Save the updated cohort
        Cohort updatedCohort = cohortRepository.save(existingCohort);
        return convertToCohortResponseDTO(updatedCohort);
    }


    @Transactional
    public void deleteCohort(Long id) {
        if (!cohortRepository.existsById(id)) {
            throw new EntityNotFoundException("Cohort not found with ID: " + id);
        }
        cohortRepository.deleteById(id);
    }


    private CohortResponseDTO convertToCohortResponseDTO(Cohort cohort) {
        // Convert Facilitator User entity to UserResponseDTO
        UserResponseDTO facilitatorDTO = null;
        if (cohort.getFacilitator() != null) {
            facilitatorDTO = UserResponseDTO.builder()
                    .id(cohort.getFacilitator().getId())
                    .fullName(cohort.getFacilitator().getFullName())
                    .email(cohort.getFacilitator().getEmail())
                    .role(cohort.getFacilitator().getRole().name()) // <--- .name() added here
                    .build();
        }

        // Convert CreatedBy User entity to UserResponseDTO
        UserResponseDTO createdByDTO = null;
        if (cohort.getCreatedBy() != null) {
            createdByDTO = UserResponseDTO.builder()
                    .id(cohort.getCreatedBy().getId())
                    .fullName(cohort.getCreatedBy().getFullName())
                    .email(cohort.getCreatedBy().getEmail())
                    .role(cohort.getCreatedBy().getRole().name()) // <--- .name() added here
                    .build();
        }

        return CohortResponseDTO.builder()
                .id(cohort.getId())
                .cohortName(cohort.getCohortName())
                .duration(cohort.getDuration())
                .startDate(cohort.getStartDate())
                .imageUrl(cohort.getImageUrl())
                .facilitator(facilitatorDTO)
                .createdBy(createdByDTO) // This field is now present in CohortResponseDTO
                .createdAt(cohort.getCreatedAt()) // This field is now present in CohortResponseDTO
                .updatedAt(cohort.getUpdatedAt()) // This field is now present in CohortResponseDTO
                .build();
    }
}