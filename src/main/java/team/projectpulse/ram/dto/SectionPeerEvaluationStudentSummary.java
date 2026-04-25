package team.projectpulse.ram.dto;

public class SectionPeerEvaluationStudentSummary {

    private Long studentId;
    private String studentName;
    private Integer evaluationCount;
    private Double averageScore;

    public SectionPeerEvaluationStudentSummary(Long studentId, String studentName, Integer evaluationCount, Double averageScore) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.evaluationCount = evaluationCount;
        this.averageScore = averageScore;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Integer getEvaluationCount() {
        return evaluationCount;
    }

    public Double getAverageScore() {
        return averageScore;
    }
}
