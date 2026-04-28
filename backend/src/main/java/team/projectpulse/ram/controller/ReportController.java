package team.projectpulse.ram.controller;

import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.projectpulse.ram.dto.SectionEvaluationReportResponse;
import team.projectpulse.ram.dto.StudentEvaluationReportResponse;
import team.projectpulse.ram.service.EvaluationService;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final EvaluationService evaluationService;

    public ReportController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/sections/{sectionId}/evaluations")
    public ResponseEntity<SectionEvaluationReportResponse> getSectionEvaluationReport(
            @PathVariable Long sectionId,
            @RequestParam LocalDate weekStart
    ) {
        return ResponseEntity.ok(evaluationService.getSectionEvaluationReport(sectionId, weekStart));
    }

    @GetMapping("/students/{studentId}/evaluations")
    public ResponseEntity<StudentEvaluationReportResponse> getStudentEvaluationReport(
            @PathVariable Long studentId,
            @RequestParam Long sectionId,
            @RequestParam LocalDate startWeek,
            @RequestParam LocalDate endWeek
    ) {
        return ResponseEntity.ok(evaluationService.getStudentEvaluationReport(studentId, sectionId, startWeek, endWeek));
    }
}
