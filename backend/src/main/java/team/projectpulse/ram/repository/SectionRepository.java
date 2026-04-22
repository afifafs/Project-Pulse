package team.projectpulse.ram.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import team.projectpulse.ram.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByNameContainingIgnoreCaseOrderByNameDesc(String name);

    List<Section> findAllByOrderByNameDesc();
}
