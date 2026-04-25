package team.projectpulse.ram.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.projectpulse.ram.model.PeerEvaluation;

public interface PeerEvaluationRepository extends JpaRepository<PeerEvaluation, Long> {

    List<PeerEvaluation> findByEvaluateeIdOrderByWeekNumberDescIdDesc(Long evaluateeId);

    List<PeerEvaluation> findByEvaluateeIdAndWeekNumberOrderByIdDesc(Long evaluateeId, Integer weekNumber);

    @Query("""
            select distinct evaluation
            from PeerEvaluation evaluation
            join fetch evaluation.evaluatee evaluatee
            join fetch evaluatee.section section
            join fetch evaluation.evaluator evaluator
            left join fetch evaluation.ratings ratings
            where section.id = :sectionId
              and (:weekNumber is null or evaluation.weekNumber = :weekNumber)
            order by evaluation.weekNumber desc, evaluatee.lastName asc, evaluatee.firstName asc
            """)
    List<PeerEvaluation> findSectionEvaluations(
            @Param("sectionId") Long sectionId,
            @Param("weekNumber") Integer weekNumber
    );
}
