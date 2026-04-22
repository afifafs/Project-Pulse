package team.projectpulse.ram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.projectpulse.ram.model.Criterion;

public interface CriterionRepository extends JpaRepository<Criterion, Long> {
}
