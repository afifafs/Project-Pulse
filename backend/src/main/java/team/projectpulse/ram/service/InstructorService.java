package team.projectpulse.ram.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.AccountSetupRequest;
import team.projectpulse.ram.dto.InstructorResponse;
import team.projectpulse.ram.dto.InviteInstructorsRequest;
import team.projectpulse.ram.exception.InvalidStudentRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Instructor;
import team.projectpulse.ram.repository.InstructorRepository;

@Service
public class InstructorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstructorService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Transactional
    public int inviteInstructors(InviteInstructorsRequest request) {
        if (request == null || request.getEmails() == null || request.getEmails().isEmpty()) {
            throw new InvalidStudentRequestException("At least one email is required.");
        }

        Set<String> emails = new LinkedHashSet<>();
        for (String email : request.getEmails()) {
            String normalized = normalizeEmail(email);
            emails.add(normalized);

            Instructor instructor = instructorRepository.findByEmailIgnoreCase(normalized)
                    .orElseGet(() -> {
                        Instructor created = new Instructor();
                        created.setEmail(normalized);
                        created.setFirstName("Invited");
                        created.setLastName("Instructor");
                        return instructorRepository.save(created);
                    });
            LOGGER.info("Simulated instructor invitation email sent to {}", instructor.getEmail());
        }

        return emails.size();
    }

    @Transactional(readOnly = true)
    public List<InstructorResponse> findInstructors(String name, String email, String sectionName) {
        return instructorRepository.search(normalizeFilter(name), normalizeFilter(email), normalizeFilter(sectionName)).stream()
                .map(InstructorResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public InstructorResponse getInstructor(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));
        return InstructorResponse.fromEntity(instructor);
    }

    @Transactional
    public InstructorResponse setupAccount(Long instructorId, AccountSetupRequest request) {
        validateAccountSetup(request);
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        instructor.setFirstName(request.getFirstName().trim());
        instructor.setLastName(request.getLastName().trim());
        instructor.setPassword(request.getPassword().trim());
        instructor.setActive(true);
        return InstructorResponse.fromEntity(instructorRepository.save(instructor));
    }

    @Transactional
    public InstructorResponse deactivate(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));
        instructor.setActive(false);
        return InstructorResponse.fromEntity(instructorRepository.save(instructor));
    }

    @Transactional
    public InstructorResponse reactivate(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));
        instructor.setActive(true);
        return InstructorResponse.fromEntity(instructorRepository.save(instructor));
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidStudentRequestException("Email values cannot be blank.");
        }
        String normalized = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidStudentRequestException("Invalid email format: " + email);
        }
        return normalized;
    }

    private void validateAccountSetup(AccountSetupRequest request) {
        if (request == null) {
            throw new InvalidStudentRequestException("Account setup request is required.");
        }
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()
                || request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new InvalidStudentRequestException("First name and last name are required.");
        }
        if (request.getPassword() == null || request.getConfirmPassword() == null
                || request.getPassword().trim().length() < 8) {
            throw new InvalidStudentRequestException("Password must be at least 8 characters.");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidStudentRequestException("Passwords do not match.");
        }
    }

    private String normalizeFilter(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
