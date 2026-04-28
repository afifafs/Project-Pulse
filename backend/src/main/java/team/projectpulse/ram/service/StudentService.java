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
import team.projectpulse.ram.dto.ProfileResponse;
import team.projectpulse.ram.dto.ProfileUpdateRequest;
import team.projectpulse.ram.dto.ResetPasswordRequest;
import team.projectpulse.ram.exception.InvalidStudentRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.repository.StudentRepository;

@Service
public class StudentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return null;
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
    public int inviteStudents(List<String> rawEmails) {
        Set<String> emails = normalizeEmails(rawEmails);

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

            LOGGER.info("Simulated invitation email sent to {}", student.getEmail());
        }

        return emails.size();
    }

    public Optional<Student> getStudentById(Long id) {
        return Optional.empty();
    }

    public List<Student> getAllStudents() {
        return List.of();
    }

    public Student updateStudent(Long id, Student student) {
        return null;
    }

    public void deleteStudent(Long id) {
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
}
