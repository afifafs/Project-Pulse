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
import team.projectpulse.ram.dto.ProfileResponse;
import team.projectpulse.ram.dto.ProfileUpdateRequest;
import team.projectpulse.ram.dto.ResetPasswordRequest;
import team.projectpulse.ram.dto.StudentDetailResponse;
import team.projectpulse.ram.dto.InviteStudentsRequest;
import team.projectpulse.ram.exception.InvalidStudentRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.repository.SectionRepository;
import team.projectpulse.ram.repository.StudentRepository;

@Service
public class StudentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;

    public StudentService(StudentRepository studentRepository, SectionRepository sectionRepository) {
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long studentId) {
        Student student = studentRepository.findByIdWithDetails(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        return ProfileResponse.fromEntity(student);
    }

    @Transactional
    public ProfileResponse updateProfile(Long studentId, ProfileUpdateRequest request) {
        validateProfileRequest(request);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (!student.getEmail().equalsIgnoreCase(normalizedEmail)
                && studentRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, studentId)) {
            throw new InvalidStudentRequestException("That email is already in use.");
        }

        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setEmail(normalizedEmail);

        return ProfileResponse.fromEntity(studentRepository.save(student));
    }

    @Transactional
    public void resetPassword(Long studentId, ResetPasswordRequest request) {
        if (request == null || request.getNewPassword() == null || request.getConfirmPassword() == null) {
            throw new InvalidStudentRequestException("Both password fields are required.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidStudentRequestException("Passwords do not match.");
        }

        if (request.getNewPassword().trim().length() < 8) {
            throw new InvalidStudentRequestException("Password must be at least 8 characters.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        student.setPassword(request.getNewPassword().trim());
        studentRepository.save(student);
        LOGGER.info("Password reset simulated for student {}", student.getEmail());
    }

    @Transactional
    public int inviteStudents(InviteStudentsRequest request) {
        if (request == null) {
            throw new InvalidStudentRequestException("Invite request is required.");
        }
        Set<String> emails = normalizeEmails(request.getEmails());
        Section section = null;
        if (request.getSectionId() != null) {
            section = sectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));
        }

        for (String email : emails) {
            Student student = studentRepository.findByEmailIgnoreCase(email)
                    .orElseGet(() -> {
                        Student created = new Student();
                        created.setEmail(email);
                        created.setFirstName("Invited");
                        created.setLastName("Student");
                        created.setPassword("student123");
                        return studentRepository.save(created);
                    });

            if (section != null) {
                student.setSection(section);
                studentRepository.save(student);
            }
            LOGGER.info("Simulated invitation email sent to {}", student.getEmail());
        }

        return emails.size();
    }

    @Transactional(readOnly = true)
    public List<StudentDetailResponse> findStudents(String name, String email, String sectionName, String teamName) {
        String nameFilter = normalizeFilter(name);
        String emailFilter = normalizeFilter(email);
        String sectionFilter = normalizeFilter(sectionName);
        String teamFilter = normalizeFilter(teamName);

        return studentRepository.findAll().stream()
                .filter(student -> matches(fullName(student), nameFilter))
                .filter(student -> matches(student.getEmail(), emailFilter))
                .filter(student -> matches(student.getSection() == null ? null : student.getSection().getName(), sectionFilter))
                .filter(student -> matches(student.getTeam() == null ? null : student.getTeam().getName(), teamFilter))
                .sorted(java.util.Comparator
                        .comparing((Student student) -> student.getSection() == null ? "" : student.getSection().getName(),
                                String.CASE_INSENSITIVE_ORDER.reversed())
                        .thenComparing(student -> student.getTeam() == null ? "" : student.getTeam().getName(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Student::getLastName, java.util.Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(Student::getFirstName, java.util.Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(StudentDetailResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentDetailResponse getStudentById(Long id) {
        Student student = studentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return StudentDetailResponse.fromEntity(student);
    }

    @Transactional
    public ProfileResponse setupStudentAccount(Long studentId, AccountSetupRequest request) {
        validateAccountSetup(request);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setPassword(request.getPassword().trim());
        student.setActive(true);

        return ProfileResponse.fromEntity(studentRepository.save(student));
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
    }

    private void validateProfileRequest(ProfileUpdateRequest request) {
        if (request == null) {
            throw new InvalidStudentRequestException("Profile request is required.");
        }

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new InvalidStudentRequestException("First name is required.");
        }

        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new InvalidStudentRequestException("Last name is required.");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidStudentRequestException("Email is required.");
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail().trim().toLowerCase()).matches()) {
            throw new InvalidStudentRequestException("Invalid email format.");
        }
    }

    private Set<String> normalizeEmails(List<String> rawEmails) {
        if (rawEmails == null || rawEmails.isEmpty()) {
            throw new InvalidStudentRequestException("At least one email is required.");
        }

        Set<String> emails = new LinkedHashSet<>();
        for (String email : rawEmails) {
            if (email == null || email.trim().isEmpty()) {
                throw new InvalidStudentRequestException("Email values cannot be blank.");
            }

            String normalizedEmail = email.trim().toLowerCase();
            if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
                throw new InvalidStudentRequestException("Invalid email format: " + email);
            }

            emails.add(normalizedEmail);
        }

        return emails;
    }

    private void validateAccountSetup(AccountSetupRequest request) {
        if (request == null) {
            throw new InvalidStudentRequestException("Account setup request is required.");
        }

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new InvalidStudentRequestException("First name is required.");
        }

        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new InvalidStudentRequestException("Last name is required.");
        }

        if (request.getPassword() == null || request.getConfirmPassword() == null) {
            throw new InvalidStudentRequestException("Both password fields are required.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidStudentRequestException("Passwords do not match.");
        }

        if (request.getPassword().trim().length() < 8) {
            throw new InvalidStudentRequestException("Password must be at least 8 characters.");
        }
    }

    private String normalizeFilter(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private boolean matches(String value, String filter) {
        if (filter == null) {
            return true;
        }
        return value != null && value.toLowerCase().contains(filter.toLowerCase());
    }

    private String fullName(Student student) {
        return ((student.getFirstName() == null ? "" : student.getFirstName()) + " "
                + (student.getLastName() == null ? "" : student.getLastName())).trim();
    }
}
