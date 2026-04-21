package team.projectpulse.ram.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.projectpulse.ram.dto.TeamResponse;
import team.projectpulse.ram.service.TeamService;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> findTeams(
            @RequestParam(required = false) String sectionName,
            @RequestParam(required = false) String teamName
    ) {
        return ResponseEntity.ok(teamService.findTeams(sectionName, teamName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamDetails(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamDetails(id));
    }
}
