package team.projectpulse.ram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.projectpulse.ram.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
}
