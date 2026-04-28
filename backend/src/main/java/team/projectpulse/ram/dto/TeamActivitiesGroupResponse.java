package team.projectpulse.ram.dto;

import java.util.List;

public class TeamActivitiesGroupResponse {

    private Long studentId;
    private String studentName;
    private List<ActivityEntryResponse> rows;

    public TeamActivitiesGroupResponse(Long studentId, String studentName, List<ActivityEntryResponse> rows) {
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

    public List<ActivityEntryResponse> getRows() {
        return rows;
    }
}
