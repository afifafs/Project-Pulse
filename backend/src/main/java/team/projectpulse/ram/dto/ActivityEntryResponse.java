package team.projectpulse.ram.dto;

import team.projectpulse.ram.model.ActivityEntry;

public class ActivityEntryResponse {

    private Long id;
    private String category;
    private String activity;
    private String description;
    private Integer plannedHours;
    private Integer actualHours;
    private String status;

    public ActivityEntryResponse(
            Long id,
            String category,
            String activity,
            String description,
            Integer plannedHours,
            Integer actualHours,
            String status
    ) {
        this.id = id;
        this.category = category;
        this.activity = activity;
        this.description = description;
        this.plannedHours = plannedHours;
        this.actualHours = actualHours;
        this.status = status;
    }

    public static ActivityEntryResponse fromEntity(ActivityEntry entry) {
        return new ActivityEntryResponse(
                entry.getId(),
                entry.getCategory(),
                entry.getActivity(),
                entry.getDescription(),
                entry.getPlannedHours(),
                entry.getActualHours(),
                entry.getStatus()
        );
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getActivity() {
        return activity;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPlannedHours() {
        return plannedHours;
    }

    public Integer getActualHours() {
        return actualHours;
    }

    public String getStatus() {
        return status;
    }
}
