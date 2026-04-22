package team.projectpulse.ram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.projectpulse.ram.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
