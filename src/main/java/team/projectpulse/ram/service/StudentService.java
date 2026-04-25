package team.projectpulse.ram.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.InviteStudentsRequest;
import team.projectpulse.ram.dto.InviteStudentsResponse;
import team.projectpulse.ram.dto.StudentAccountRequest;
import team.projectpulse.ram.dto.StudentAccountResponse;
import team.projectpulse.ram.exception.DuplicateResourceException;
import team.projectpulse.ram.exception.InvalidAccountRequestException;
import team.projectpulse.ram.exception.InvalidStudentInviteRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.model.Team;
import team.projectpulse.ram.repository.SectionRepository;
import team.projectpulse.ram.repository.StudentRepository;
import team.projectpulse.ram.repository.TeamRepository;

@Service
public class StudentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final TeamRepository teamRepository;

    public StudentService(
            StudentRepository studentRepository,
            SectionRepository sectionRepository,
            TeamRepository teamRepository
    ) {
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public InviteStudentsResponse inviteStudents(InviteStudentsRequest request) {
        Set<String> emails = normalizeAndValidateEmails(request);

        for (String email : emails) {
            Student student = studentRepository.findByEmailIgnoreCase(email)
                    .orElseGet(() -> createStudentForEmail(email));

            LOGGER.info("Simulated invitation email sent to {}", student.getEmail());
        }

        return new InviteStudentsResponse(emails.size());
    }

    @Transactional
    public StudentAccountResponse setupStudentAccount(StudentAccountRequest request) {
        validateStudentAccountRequest(request, true);

        Student student = resolveStudentForSetup(request);
        applyStudentAccount(student, request);
        student.setActive(Boolean.TRUE);

        Student savedStudent = studentRepository.save(student);
        return StudentAccountResponse.fromEntity(savedStudent);
    }

    @Transactional
    public StudentAccountResponse updateStudentAccount(Long id, StudentAccountRequest request) {
        validateStudentAccountRequest(request, false);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        applyStudentAccount(student, request);
        Student savedStudent = studentRepository.save(student);
        return StudentAccountResponse.fromEntity(savedStudent);
    }

    @Transactional(readOnly = true)
    public StudentAccountResponse getStudentAccount(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        return StudentAccountResponse.fromEntity(student);
    }

    @Transactional(readOnly = true)
    public List<StudentAccountResponse> getStudentAccounts() {
        return studentRepository.findAll().stream()
                .map(StudentAccountResponse::fromEntity)
                .toList();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    private Student resolveStudentForSetup(StudentAccountRequest request) {
        if (request.getStudentId() != null) {
            return studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));
        }

        return studentRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseGet(Student::new);
    }

    private void applyStudentAccount(Student student, StudentAccountRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String normalizedUsername = request.getUsername().trim().toLowerCase();

        if (student.getId() == null) {
            if (studentRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()
                    && (student.getEmail() == null || !student.getEmail().equalsIgnoreCase(normalizedEmail))) {
                throw new DuplicateResourceException("A student with this email already exists.");
            }

            if (studentRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
                throw new DuplicateResourceException("A student with this username already exists.");
            }
        } else {
            if (studentRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, student.getId())) {
                throw new DuplicateResourceException("A student with this email already exists.");
            }

            if (studentRepository.existsByUsernameIgnoreCaseAndIdNot(normalizedUsername, student.getId())) {
                throw new DuplicateResourceException("A student with this username already exists.");
            }
        }

        Team team = request.getTeamId() == null
                ? null
                : teamRepository.findById(request.getTeamId())
                        .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + request.getTeamId()));

        Section section;
        if (request.getSectionId() != null) {
            section = sectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));
        } else if (team != null) {
            section = team.getSection();
        } else {
            throw new InvalidAccountRequestException("sectionId is required when no team is selected.");
        }

        if (team != null && team.getSection() != null && !team.getSection().getId().equals(section.getId())) {
            throw new InvalidAccountRequestException("Selected team does not belong to the selected section.");
        }

        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setEmail(normalizedEmail);
        student.setUsername(normalizedUsername);
        student.setPassword(request.getPassword().trim());
        student.setSection(section);
        student.setTeam(team);
    }

    private void validateStudentAccountRequest(StudentAccountRequest request, boolean requirePassword) {
        if (request == null) {
            throw new InvalidAccountRequestException("Student account request is required.");
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

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new InvalidAccountRequestException("Email must be valid.");
        }

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new InvalidAccountRequestException("Username is required.");
        }

        if ((requirePassword || (request.getPassword() != null && !request.getPassword().trim().isEmpty()))
                && (request.getPassword() == null || request.getPassword().trim().length() < 8)) {
            throw new InvalidAccountRequestException("Password must be at least 8 characters long.");
        }

        if (request.getSectionId() == null && request.getTeamId() == null) {
            throw new InvalidAccountRequestException("Provide a sectionId or teamId.");
        }
    }

    private Student createStudentForEmail(String email) {
        Student student = new Student();
        student.setEmail(email);
        student.setActive(Boolean.FALSE);
        return studentRepository.save(student);
    }

    private Set<String> normalizeAndValidateEmails(InviteStudentsRequest request) {
        if (request == null || request.getEmails() == null || request.getEmails().isEmpty()) {
            throw new InvalidStudentInviteRequestException("At least one email is required.");
        }

        Set<String> emails = new LinkedHashSet<>();
        for (String email : request.getEmails()) {
            if (email == null || email.trim().isEmpty()) {
                throw new InvalidStudentInviteRequestException("Email values cannot be blank.");
            }

            String normalizedEmail = email.trim().toLowerCase();
            if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
                throw new InvalidStudentInviteRequestException("Invalid email format: " + email);
            }

            emails.add(normalizedEmail);
        }

        return emails;
    }
}
