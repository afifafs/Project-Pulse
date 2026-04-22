package team.projectpulse.ram.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import team.projectpulse.ram.model.Team;
import team.projectpulse.ram.repository.TeamRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team createTeam(Team team) {
        return null;
    }

    public Optional<Team> getTeamById(Long id) {
        return Optional.empty();
    }

    public List<Team> getAllTeams() {
        return List.of();
    }

    public Team updateTeam(Long id, Team team) {
        return null;
    }

    public void deleteTeam(Long id) {
    }
}
