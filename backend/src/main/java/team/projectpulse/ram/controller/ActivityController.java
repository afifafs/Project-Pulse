package team.projectpulse.ram.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.projectpulse.ram.dto.ActivityEntryRequest;
import team.projectpulse.ram.dto.StudentActivitiesResponse;
import team.projectpulse.ram.dto.TeamActivitiesResponse;
import team.projectpulse.ram.service.ActivityService;

@RestController
@RequestMapping
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/students/{studentId}/activities")
    public ResponseEntity<StudentActivitiesResponse> getStudentActivities(
            @PathVariable Long studentId,
            @RequestParam LocalDate weekStart
    ) {
        return ResponseEntity.ok(activityService.getStudentActivities(studentId, weekStart));
    }

    @PutMapping("/students/{studentId}/activities")
    public ResponseEntity<StudentActivitiesResponse> saveStudentActivities(
            @PathVariable Long studentId,
            @RequestParam LocalDate weekStart,
            @RequestBody List<ActivityEntryRequest> requests
    ) {
        return ResponseEntity.ok(activityService.saveStudentActivities(studentId, weekStart, requests));
    }

    @GetMapping("/sections/{sectionId}/activities")
    public ResponseEntity<TeamActivitiesResponse> getSectionActivities(
            @PathVariable Long sectionId,
            @RequestParam LocalDate weekStart
    ) {
        return ResponseEntity.ok(activityService.getSectionActivities(sectionId, weekStart));
    }
}
