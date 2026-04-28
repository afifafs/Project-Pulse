package team.projectpulse.ram.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.projectpulse.ram.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Student> findByIdIn(Collection<Long> ids);

    @Query("""
            select student
            from Student student
            left join fetch student.team team
            left join fetch student.section section
            where student.id = :id
            """)
    Optional<Student> findByIdWithDetails(@Param("id") Long id);

    @Query("""
            select student
            from Student student
            left join fetch student.team team
            left join fetch student.section section
            where section.id = :sectionId
            order by student.lastName asc, student.firstName asc
            """)
    List<Student> findAllBySectionIdWithDetails(@Param("sectionId") Long sectionId);
}
