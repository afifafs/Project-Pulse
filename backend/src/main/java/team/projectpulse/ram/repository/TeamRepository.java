package team.projectpulse.ram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.projectpulse.ram.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
