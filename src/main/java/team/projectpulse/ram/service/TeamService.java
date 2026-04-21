package team.projectpulse.ram.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.TeamResponse;
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

    @Transactional(readOnly = true)
    public List<TeamResponse> findTeams(String sectionName, String teamName) {
        return teamRepository.findTeams(normalizeFilter(sectionName), normalizeFilter(teamName)).stream()
                .map(TeamResponse::fromEntity)
                .toList();
    }

    public List<Team> getAllTeams() {
        return List.of();
    }

    public Team updateTeam(Long id, Team team) {
        return null;
    }

    public void deleteTeam(Long id) {
    }

    private String normalizeFilter(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}
