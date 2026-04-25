package team.projectpulse.ram.dto;

import java.util.List;

public class StudentWarReportResponse {

    private Long studentId;
    private String studentName;
    private Integer weekNumber;
    private Integer activityCount;
    private Double totalPlannedHours;
    private Double totalActualHours;
    private List<WarActivityResponse> activities;

    public StudentWarReportResponse(
            Long studentId,
            String studentName,
            Integer weekNumber,
            Integer activityCount,
            Double totalPlannedHours,
            Double totalActualHours,
            List<WarActivityResponse> activities
    ) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.weekNumber = weekNumber;
        this.activityCount = activityCount;
        this.totalPlannedHours = totalPlannedHours;
        this.totalActualHours = totalActualHours;
        this.activities = activities;
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

    public Integer getActivityCount() {
        return activityCount;
    }

    public Double getTotalPlannedHours() {
        return totalPlannedHours;
    }

    public Double getTotalActualHours() {
        return totalActualHours;
    }

    public List<WarActivityResponse> getActivities() {
        return activities;
    }
}
