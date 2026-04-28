package team.projectpulse.ram.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team.projectpulse.ram.dto.AssignInstructorsRequest;
import team.projectpulse.ram.dto.AssignStudentsRequest;
import team.projectpulse.ram.dto.TeamDetailResponse;
import team.projectpulse.ram.dto.TeamRequest;
import team.projectpulse.ram.service.TeamService;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<TeamDetailResponse>> findTeams(
            @RequestParam(required = false) String sectionName,
            @RequestParam(required = false) String teamName
    ) {
        return ResponseEntity.ok(teamService.getAllTeams(sectionName, teamName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDetailResponse> getTeam(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @PostMapping
    public ResponseEntity<TeamDetailResponse> createTeam(@RequestBody TeamRequest request) {
        TeamDetailResponse created = teamService.createTeam(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDetailResponse> updateTeam(@PathVariable Long id, @RequestBody TeamRequest request) {
        return ResponseEntity.ok(teamService.updateTeam(id, request));
    }

    @PutMapping("/{id}/students")
    public ResponseEntity<TeamDetailResponse> assignStudents(
            @PathVariable Long id,
            @RequestBody AssignStudentsRequest request
    ) {
        return ResponseEntity.ok(teamService.assignStudents(id, request));
    }

    @DeleteMapping("/{teamId}/students/{studentId}")
    public ResponseEntity<TeamDetailResponse> removeStudent(
            @PathVariable Long teamId,
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(teamService.removeStudent(teamId, studentId));
    }

    @PutMapping("/{id}/instructors")
    public ResponseEntity<TeamDetailResponse> assignInstructors(
            @PathVariable Long id,
            @RequestBody AssignInstructorsRequest request
    ) {
        return ResponseEntity.ok(teamService.assignInstructors(id, request));
    }

    @DeleteMapping("/{teamId}/instructors/{instructorId}")
    public ResponseEntity<TeamDetailResponse> removeInstructor(
            @PathVariable Long teamId,
            @PathVariable Long instructorId
    ) {
        return ResponseEntity.ok(teamService.removeInstructor(teamId, instructorId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}
