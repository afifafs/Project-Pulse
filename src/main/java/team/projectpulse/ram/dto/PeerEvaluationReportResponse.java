package team.projectpulse.ram.dto;

import java.util.List;

public class PeerEvaluationReportResponse {

    private Long studentId;
    private String studentName;
    private Integer weekNumber;
    private Integer evaluationCount;
    private Double averageScore;
    private List<PeerEvaluationResponse> evaluations;

    public PeerEvaluationReportResponse(
            Long studentId,
            String studentName,
            Integer weekNumber,
            Integer evaluationCount,
            Double averageScore,
            List<PeerEvaluationResponse> evaluations
    ) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.weekNumber = weekNumber;
        this.evaluationCount = evaluationCount;
        this.averageScore = averageScore;
        this.evaluations = evaluations;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public Integer getEvaluationCount() {
        return evaluationCount;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public List<PeerEvaluationResponse> getEvaluations() {
        return evaluations;
    }
}
