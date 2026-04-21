package team.projectpulse.ram.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return null;
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
}
