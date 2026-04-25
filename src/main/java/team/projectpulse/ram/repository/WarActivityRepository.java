package team.projectpulse.ram.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.projectpulse.ram.model.WarActivity;

public interface WarActivityRepository extends JpaRepository<WarActivity, Long> {

    List<WarActivity> findByStudentIdOrderByWeekNumberDescIdDesc(Long studentId);

    List<WarActivity> findByStudentIdAndWeekNumberOrderByIdDesc(Long studentId, Integer weekNumber);

    List<WarActivity> findByTeamIdOrderByWeekNumberDescIdDesc(Long teamId);

    @Query("""
            select activity
            from WarActivity activity
            where activity.team.id = :teamId
              and activity.weekNumber = :weekNumber
            order by activity.student.id asc, activity.id asc
            """)
    List<WarActivity> findTeamActivitiesForWeek(
            @Param("teamId") Long teamId,
            @Param("weekNumber") Integer weekNumber
    );
}
