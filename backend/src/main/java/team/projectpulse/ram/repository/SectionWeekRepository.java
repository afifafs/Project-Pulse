package team.projectpulse.ram.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team.projectpulse.ram.model.SectionWeek;

public interface SectionWeekRepository extends JpaRepository<SectionWeek, Long> {

    List<SectionWeek> findAllBySectionIdOrderByWeekStartAsc(Long sectionId);

    void deleteAllBySectionId(Long sectionId);

    Optional<SectionWeek> findBySectionIdAndWeekStart(Long sectionId, LocalDate weekStart);
}
