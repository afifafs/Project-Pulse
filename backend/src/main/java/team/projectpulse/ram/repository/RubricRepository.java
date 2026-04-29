package team.projectpulse.ram.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import team.projectpulse.ram.model.Rubric;

public interface RubricRepository extends JpaRepository<Rubric, Long> {

    boolean existsByNameIgnoreCase(String name);

    @Query("""
            select distinct rubric
            from Rubric rubric
            left join fetch rubric.criteria criteria
            order by rubric.name asc, criteria.id asc
            """)
    List<Rubric> findAllWithCriteria();
}
