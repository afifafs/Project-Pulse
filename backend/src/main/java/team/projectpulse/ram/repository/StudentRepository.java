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

    @Query("""
            select student
            from Student student
            left join fetch student.team team
            left join fetch student.section section
            where (:name is null or lower(concat(coalesce(student.firstName, ''), ' ', coalesce(student.lastName, ''))) like lower(concat('%', :name, '%')))
              and (:email is null or lower(student.email) like lower(concat('%', :email, '%')))
              and (:sectionName is null or lower(section.name) like lower(concat('%', :sectionName, '%')))
              and (:teamName is null or lower(team.name) like lower(concat('%', :teamName, '%')))
            order by section.name desc, team.name asc, student.lastName asc, student.firstName asc
            """)
    List<Student> search(
            @Param("name") String name,
            @Param("email") String email,
            @Param("sectionName") String sectionName,
            @Param("teamName") String teamName
    );
}
