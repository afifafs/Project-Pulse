package team.projectpulse.ram.dto;

public class TeamWarStudentSummary {

    private Long studentId;
    private String studentName;
    private Integer activityCount;
    private Double plannedHours;
    private Double actualHours;

    public TeamWarStudentSummary(Long studentId, String studentName, Integer activityCount, Double plannedHours, Double actualHours) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.activityCount = activityCount;
        this.plannedHours = plannedHours;
        this.actualHours = actualHours;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Integer getActivityCount() {
        return activityCount;
    }

    public Double getPlannedHours() {
        return plannedHours;
    }

    public Double getActualHours() {
        return actualHours;
    }
}
