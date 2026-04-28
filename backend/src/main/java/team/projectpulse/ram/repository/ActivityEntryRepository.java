package team.projectpulse.ram.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import team.projectpulse.ram.model.ActivityEntry;

public interface ActivityEntryRepository extends JpaRepository<ActivityEntry, Long> {

    List<ActivityEntry> findAllByStudentIdAndWeekStartOrderByIdAsc(Long studentId, LocalDate weekStart);

    List<ActivityEntry> findAllBySectionIdAndWeekStartOrderByStudentLastNameAscStudentFirstNameAscIdAsc(
            Long sectionId,
            LocalDate weekStart
    );

    void deleteAllByStudentIdAndWeekStart(Long studentId, LocalDate weekStart);
}
