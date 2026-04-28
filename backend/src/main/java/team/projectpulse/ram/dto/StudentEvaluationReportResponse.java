package team.projectpulse.ram.dto;

import java.util.List;

public class StudentEvaluationReportResponse {

    private Long studentId;
    private String studentName;
    private List<StudentEvaluationWeekDetailResponse> rows;

    public StudentEvaluationReportResponse(Long studentId, String studentName, List<StudentEvaluationWeekDetailResponse> rows) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.rows = rows;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public List<StudentEvaluationWeekDetailResponse> getRows() {
        return rows;
    }
}
