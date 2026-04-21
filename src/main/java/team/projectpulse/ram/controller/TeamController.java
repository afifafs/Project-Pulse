package team.projectpulse.ram.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team.projectpulse.ram.dto.AssignStudentsRequest;
import team.projectpulse.ram.dto.TeamRequest;
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

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@RequestBody TeamRequest request) {
        TeamResponse createdTeam = teamService.createTeam(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTeam.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdTeam);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamDetails(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamDetails(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long id,
            @RequestBody TeamRequest request
    ) {
        return ResponseEntity.ok(teamService.updateTeam(id, request));
    }

    @PutMapping("/{teamId}/students")
    public ResponseEntity<TeamResponse> assignStudentsToTeam(
            @PathVariable Long teamId,
            @RequestBody AssignStudentsRequest request
    ) {
        return ResponseEntity.ok(teamService.assignStudentsToTeam(teamId, request));
    }
}
