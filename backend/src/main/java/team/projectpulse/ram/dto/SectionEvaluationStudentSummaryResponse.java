package team.projectpulse.ram.dto;

import java.util.List;

public class SectionEvaluationStudentSummaryResponse {

    private Long studentId;
    private String studentName;
    private Double averageTotalScore;
    private List<Double> criterionScores;
    private int submissionCount;

    public SectionEvaluationStudentSummaryResponse(
            Long studentId,
            String studentName,
            Double averageTotalScore,
            List<Double> criterionScores,
            int submissionCount
    ) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.averageTotalScore = averageTotalScore;
        this.criterionScores = criterionScores;
        this.submissionCount = submissionCount;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Double getAverageTotalScore() {
        return averageTotalScore;
    }

    public List<Double> getCriterionScores() {
        return criterionScores;
    }

    public int getSubmissionCount() {
        return submissionCount;
    }
}
