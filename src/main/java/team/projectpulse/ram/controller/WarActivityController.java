package team.projectpulse.ram.controller;

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
import team.projectpulse.ram.dto.StudentWarReportResponse;
import team.projectpulse.ram.dto.TeamWarReportResponse;
import team.projectpulse.ram.dto.WarActivityRequest;
import team.projectpulse.ram.dto.WarActivityResponse;
import team.projectpulse.ram.service.WarActivityService;

@RestController
@RequestMapping
public class WarActivityController {

    private final WarActivityService warActivityService;

    public WarActivityController(WarActivityService warActivityService) {
        this.warActivityService = warActivityService;
    }

    @PostMapping("/activities")
    public ResponseEntity<WarActivityResponse> createActivity(@RequestBody WarActivityRequest request) {
        return ResponseEntity.ok(warActivityService.createActivity(request));
    }

    @PutMapping("/activities/{id}")
    public ResponseEntity<WarActivityResponse> updateActivity(
            @PathVariable Long id,
            @RequestBody WarActivityRequest request
    ) {
        return ResponseEntity.ok(warActivityService.updateActivity(id, request));
    }

    @DeleteMapping("/activities/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        warActivityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students/{studentId}/activities")
    public ResponseEntity<List<WarActivityResponse>> getStudentActivities(
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer weekNumber
    ) {
        return ResponseEntity.ok(warActivityService.getStudentActivities(studentId, weekNumber));
    }

    @GetMapping("/students/{studentId}/war-report")
    public ResponseEntity<StudentWarReportResponse> getStudentWarReport(
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer weekNumber
    ) {
        return ResponseEntity.ok(warActivityService.getStudentWarReport(studentId, weekNumber));
    }

    @GetMapping("/teams/{teamId}/war-report")
    public ResponseEntity<TeamWarReportResponse> getTeamWarReport(
            @PathVariable Long teamId,
            @RequestParam String viewerType,
            @RequestParam Long viewerId,
            @RequestParam(required = false) Integer weekNumber
    ) {
        return ResponseEntity.ok(warActivityService.getTeamWarReport(teamId, viewerType, viewerId, weekNumber));
    }

    @GetMapping("/instructors/{instructorId}/students/{studentId}/war-report")
    public ResponseEntity<StudentWarReportResponse> getInstructorStudentWarReport(
            @PathVariable Long instructorId,
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer weekNumber
    ) {
        return ResponseEntity.ok(warActivityService.getInstructorStudentWarReport(instructorId, studentId, weekNumber));
    }
}
