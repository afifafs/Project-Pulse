package team.projectpulse.ram.dto;

import java.util.List;

public class TeamWarReportResponse {

    private Long teamId;
    private String teamName;
    private Integer weekNumber;
    private String viewerType;
    private Integer activityCount;
    private Double totalPlannedHours;
    private Double totalActualHours;
    private List<TeamWarStudentSummary> studentSummaries;
    private List<WarActivityResponse> activities;

    public TeamWarReportResponse(
            Long teamId,
            String teamName,
            Integer weekNumber,
            String viewerType,
            Integer activityCount,
            Double totalPlannedHours,
            Double totalActualHours,
            List<TeamWarStudentSummary> studentSummaries,
            List<WarActivityResponse> activities
    ) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.weekNumber = weekNumber;
        this.viewerType = viewerType;
        this.activityCount = activityCount;
        this.totalPlannedHours = totalPlannedHours;
        this.totalActualHours = totalActualHours;
        this.studentSummaries = studentSummaries;
        this.activities = activities;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public String getViewerType() {
        return viewerType;
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

    public List<TeamWarStudentSummary> getStudentSummaries() {
        return studentSummaries;
    }

    public List<WarActivityResponse> getActivities() {
        return activities;
    }
}
