package team.projectpulse.ram.dto;

import team.projectpulse.ram.model.WarActivity;

public class WarActivityResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long teamId;
    private String teamName;
    private String title;
    private String description;
    private String comments;
    private String category;
    private String status;
    private Double plannedHours;
    private Double actualHours;
    private Integer weekNumber;

    public WarActivityResponse(
            Long id,
            Long studentId,
            String studentName,
            Long teamId,
            String teamName,
            String title,
            String description,
            String comments,
            String category,
            String status,
            Double plannedHours,
            Double actualHours,
            Integer weekNumber
    ) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.title = title;
        this.description = description;
        this.comments = comments;
        this.category = category;
        this.status = status;
        this.plannedHours = plannedHours;
        this.actualHours = actualHours;
        this.weekNumber = weekNumber;
    }

    public static WarActivityResponse fromEntity(WarActivity activity) {
        String studentName = activity.getStudent() == null
                ? null
                : activity.getStudent().getFirstName() + " " + activity.getStudent().getLastName();

        return new WarActivityResponse(
                activity.getId(),
                activity.getStudent() == null ? null : activity.getStudent().getId(),
                studentName,
                activity.getTeam() == null ? null : activity.getTeam().getId(),
                activity.getTeam() == null ? null : activity.getTeam().getName(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getComments(),
                activity.getCategory() == null ? null : activity.getCategory().name(),
                activity.getStatus() == null ? null : activity.getStatus().name(),
                activity.getPlannedHours(),
                activity.getActualHours(),
                activity.getWeekNumber()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getComments() {
        return comments;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public Double getPlannedHours() {
        return plannedHours;
    }

    public Double getActualHours() {
        return actualHours;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }
}
