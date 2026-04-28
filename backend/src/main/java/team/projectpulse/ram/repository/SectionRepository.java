package team.projectpulse.ram.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.projectpulse.ram.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByNameContainingIgnoreCaseOrderByNameDesc(String name);

    List<Section> findAllByOrderByNameDesc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("""
            select distinct section
            from Section section
            left join fetch section.rubric rubric
            left join fetch rubric.criteria criteria
            left join fetch section.teams teams
            left join fetch teams.students teamStudents
            left join fetch teams.instructors teamInstructors
            left join fetch section.students students
            left join fetch section.instructors instructors
            left join fetch section.weeks weeks
            where section.id = :id
            """)
    java.util.Optional<Section> findByIdWithDetails(@Param("id") Long id);
}
