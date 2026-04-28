package team.projectpulse.ram.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.projectpulse.ram.model.Instructor;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    Optional<Instructor> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Instructor> findByIdIn(Collection<Long> ids);

    @Query("""
            select distinct instructor
            from Instructor instructor
            left join Team team on instructor member of team.instructors
            left join team.section section
            where (:name is null or lower(concat(coalesce(instructor.firstName, ''), ' ', coalesce(instructor.lastName, ''))) like lower(concat('%', :name, '%')))
              and (:email is null or lower(instructor.email) like lower(concat('%', :email, '%')))
              and (:sectionName is null or lower(section.name) like lower(concat('%', :sectionName, '%')))
            order by instructor.lastName asc, instructor.firstName asc
            """)
    List<Instructor> search(
            @Param("name") String name,
            @Param("email") String email,
            @Param("sectionName") String sectionName
    );
}
