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
import team.projectpulse.ram.exception.InvalidStudentInviteRequestException;
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

    private Student createStudentForEmail(String email) {
        Student student = new Student();
        student.setEmail(email);
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
