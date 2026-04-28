package team.projectpulse.ram.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.projectpulse.ram.model.PeerEvaluation;

public interface PeerEvaluationRepository extends JpaRepository<PeerEvaluation, Long> {

    @Query("""
            select distinct evaluation
            from PeerEvaluation evaluation
            left join fetch evaluation.scores scores
            left join fetch scores.criterion criterion
            where evaluation.reviewee.id = :revieweeId
              and evaluation.section.id = :sectionId
              and evaluation.weekStart between :startWeek and :endWeek
            order by evaluation.weekStart asc
            """)
    List<PeerEvaluation> findAllForRevieweeBetweenWeeks(
            @Param("revieweeId") Long revieweeId,
            @Param("sectionId") Long sectionId,
            @Param("startWeek") LocalDate startWeek,
            @Param("endWeek") LocalDate endWeek
    );

    @Query("""
            select distinct evaluation
            from PeerEvaluation evaluation
            left join fetch evaluation.scores scores
            left join fetch scores.criterion criterion
            where evaluation.reviewer.id = :reviewerId
              and evaluation.reviewee.id = :revieweeId
              and evaluation.section.id = :sectionId
              and evaluation.weekStart = :weekStart
            """)
    Optional<PeerEvaluation> findExistingSubmission(
            @Param("reviewerId") Long reviewerId,
            @Param("revieweeId") Long revieweeId,
            @Param("sectionId") Long sectionId,
            @Param("weekStart") LocalDate weekStart
    );

    @Query("""
            select distinct evaluation
            from PeerEvaluation evaluation
            left join fetch evaluation.reviewee reviewee
            left join fetch evaluation.reviewer reviewer
            left join fetch evaluation.scores scores
            left join fetch scores.criterion criterion
            where evaluation.section.id = :sectionId
              and evaluation.weekStart = :weekStart
            order by reviewee.lastName asc, reviewee.firstName asc
            """)
    List<PeerEvaluation> findAllBySectionAndWeek(
            @Param("sectionId") Long sectionId,
            @Param("weekStart") LocalDate weekStart
    );
}
