package team.projectpulse.ram.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.projectpulse.ram.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("""
            select distinct team
            from Team team
            left join fetch team.section section
            left join fetch team.students students
            where (:sectionName is null or lower(section.name) like lower(concat('%', :sectionName, '%')))
              and (:teamName is null or lower(team.name) like lower(concat('%', :teamName, '%')))
            order by section.name desc, team.name asc
            """)
    List<Team> findTeams(
            @Param("sectionName") String sectionName,
            @Param("teamName") String teamName
    );

    @Query("""
            select distinct team
            from Team team
            left join fetch team.section section
            left join fetch team.students students
            where team.id = :id
            """)
    Optional<Team> findByIdWithDetails(@Param("id") Long id);
}
