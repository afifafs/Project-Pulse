package team.projectpulse.ram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.projectpulse.ram.model.Rubric;

public interface RubricRepository extends JpaRepository<Rubric, Long> {

    boolean existsByNameIgnoreCase(String name);
}
