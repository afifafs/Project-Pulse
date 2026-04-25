package team.projectpulse.ram.dto;

import java.util.List;

public class SectionPeerEvaluationReportResponse {

    private Long sectionId;
    private String sectionName;
    private Integer weekNumber;
    private Integer submittedEvaluationCount;
    private Double averageScore;
    private List<SectionPeerEvaluationStudentSummary> students;

    public SectionPeerEvaluationReportResponse(
            Long sectionId,
            String sectionName,
            Integer weekNumber,
            Integer submittedEvaluationCount,
            Double averageScore,
            List<SectionPeerEvaluationStudentSummary> students
    ) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.weekNumber = weekNumber;
        this.submittedEvaluationCount = submittedEvaluationCount;
        this.averageScore = averageScore;
        this.students = students;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public Integer getSubmittedEvaluationCount() {
        return submittedEvaluationCount;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public List<SectionPeerEvaluationStudentSummary> getStudents() {
        return students;
    }
}
