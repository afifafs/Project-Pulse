package team.projectpulse.ram.controller;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.projectpulse.ram.dto.EvaluationHistoryResponse;
import team.projectpulse.ram.dto.PeerEvaluationBatchRequest;
import team.projectpulse.ram.service.EvaluationService;

@RestController
@RequestMapping("/students/{studentId}/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping
    public ResponseEntity<EvaluationHistoryResponse> getEvaluationHistory(
            @PathVariable Long studentId,
            @RequestParam Long sectionId,
            @RequestParam LocalDate startWeek,
            @RequestParam LocalDate endWeek
    ) {
        return ResponseEntity.ok(evaluationService.getEvaluationHistory(studentId, sectionId, startWeek, endWeek));
    }

    @PostMapping
    public ResponseEntity<Map<String, Integer>> savePeerEvaluations(
            @PathVariable Long studentId,
            @RequestParam Long sectionId,
            @RequestParam LocalDate weekStart,
            @RequestBody PeerEvaluationBatchRequest request
    ) {
        int savedCount = evaluationService.savePeerEvaluations(studentId, sectionId, weekStart, request);
        return ResponseEntity.ok(Map.of("savedCount", savedCount));
    }
}
