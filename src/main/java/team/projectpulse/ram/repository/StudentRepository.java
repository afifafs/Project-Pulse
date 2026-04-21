package team.projectpulse.ram.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team.projectpulse.ram.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmailIgnoreCase(String email);

    List<Student> findByIdIn(Collection<Long> ids);
}
