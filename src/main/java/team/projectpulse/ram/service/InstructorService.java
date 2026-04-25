package team.projectpulse.ram.service;

import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.InstructorAccountRequest;
import team.projectpulse.ram.dto.InstructorResponse;
import team.projectpulse.ram.exception.DuplicateResourceException;
import team.projectpulse.ram.exception.InvalidAccountRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Instructor;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.repository.InstructorRepository;
import team.projectpulse.ram.repository.SectionRepository;

@Service
public class InstructorService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final InstructorRepository instructorRepository;
    private final SectionRepository sectionRepository;

    public InstructorService(InstructorRepository instructorRepository, SectionRepository sectionRepository) {
        this.instructorRepository = instructorRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public InstructorResponse setupInstructorAccount(InstructorAccountRequest request) {
        validateInstructorRequest(request);

        Instructor instructor = resolveInstructor(request);
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String normalizedUsername = request.getUsername().trim().toLowerCase();

        if (instructor.getId() == null) {
            if (instructorRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                throw new DuplicateResourceException("An instructor with this email already exists.");
            }

            if (instructorRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
                throw new DuplicateResourceException("An instructor with this username already exists.");
            }
        } else {
            if (instructorRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, instructor.getId())) {
                throw new DuplicateResourceException("An instructor with this email already exists.");
            }

            if (instructorRepository.existsByUsernameIgnoreCaseAndIdNot(normalizedUsername, instructor.getId())) {
                throw new DuplicateResourceException("An instructor with this username already exists.");
            }
        }

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));

        instructor.setFirstName(request.getFirstName().trim());
        instructor.setLastName(request.getLastName().trim());
        instructor.setEmail(normalizedEmail);
        instructor.setUsername(normalizedUsername);
        instructor.setPassword(request.getPassword().trim());
        instructor.setSection(section);
        instructor.setActive(Boolean.TRUE);

        Instructor savedInstructor = instructorRepository.save(instructor);
        return InstructorResponse.fromEntity(savedInstructor);
    }

    @Transactional
    public InstructorResponse reactivateInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));

        instructor.setActive(Boolean.TRUE);
        return InstructorResponse.fromEntity(instructorRepository.save(instructor));
    }

    @Transactional(readOnly = true)
    public List<InstructorResponse> getInstructors() {
        return instructorRepository.findAll().stream()
                .map(InstructorResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Instructor getInstructorEntity(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
    }

    private Instructor resolveInstructor(InstructorAccountRequest request) {
        if (request.getInstructorId() != null) {
            return instructorRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Instructor not found with id: " + request.getInstructorId()
                    ));
        }

        return instructorRepository.findByEmailIgnoreCase(request.getEmail().trim()).orElseGet(Instructor::new);
    }

    private void validateInstructorRequest(InstructorAccountRequest request) {
        if (request == null) {
            throw new InvalidAccountRequestException("Instructor account request is required.");
        }

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new InvalidAccountRequestException("First name is required.");
        }

        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new InvalidAccountRequestException("Last name is required.");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidAccountRequestException("Email is required.");
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail().trim().toLowerCase()).matches()) {
            throw new InvalidAccountRequestException("Email must be valid.");
        }

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new InvalidAccountRequestException("Username is required.");
        }

        if (request.getPassword() == null || request.getPassword().trim().length() < 8) {
            throw new InvalidAccountRequestException("Password must be at least 8 characters long.");
        }

        if (request.getSectionId() == null) {
            throw new InvalidAccountRequestException("sectionId is required.");
        }
    }
}
