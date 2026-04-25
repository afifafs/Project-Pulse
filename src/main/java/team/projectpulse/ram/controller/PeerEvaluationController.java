package team.projectpulse.ram.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.projectpulse.ram.dto.PeerEvaluationReportResponse;
import team.projectpulse.ram.dto.PeerEvaluationRequest;
import team.projectpulse.ram.dto.PeerEvaluationResponse;
import team.projectpulse.ram.dto.SectionPeerEvaluationReportResponse;
import team.projectpulse.ram.service.PeerEvaluationService;

@RestController
@RequestMapping
public class PeerEvaluationController {

    private final PeerEvaluationService peerEvaluationService;

    public PeerEvaluationController(PeerEvaluationService peerEvaluationService) {
        this.peerEvaluationService = peerEvaluationService;
    }

    @PostMapping("/peer-evaluations")
    public ResponseEntity<PeerEvaluationResponse> submitEvaluation(@RequestBody PeerEvaluationRequest request) {
        return ResponseEntity.ok(peerEvaluationService.submitEvaluation(request));
    }

    @GetMapping("/students/{studentId}/peer-evaluation-report")
    public ResponseEntity<PeerEvaluationReportResponse> getStudentPeerEvaluationReport(
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer weekNumber
    ) {
        return ResponseEntity.ok(peerEvaluationService.getStudentPeerEvaluationReport(studentId, weekNumber, false));
    }

    @GetMapping("/instructors/{instructorId}/sections/{sectionId}/peer-evaluation-report")
    public ResponseEntity<SectionPeerEvaluationReportResponse> getSectionPeerEvaluationReport(
            @PathVariable Long instructorId,
            @PathVariable Long sectionId,
            @RequestParam(required = false) Integer weekNumber
    ) {
        return ResponseEntity.ok(peerEvaluationService.getSectionPeerEvaluationReport(instructorId, sectionId, weekNumber));
    }

    @GetMapping("/instructors/{instructorId}/students/{studentId}/peer-evaluation-report")
    public ResponseEntity<PeerEvaluationReportResponse> getInstructorStudentPeerEvaluationReport(
            @PathVariable Long instructorId,
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer weekNumber
    ) {
        return ResponseEntity.ok(
                peerEvaluationService.getInstructorStudentReport(instructorId, studentId, weekNumber)
        );
    }
}
