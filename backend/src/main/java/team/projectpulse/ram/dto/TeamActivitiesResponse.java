package team.projectpulse.ram.dto;

import java.time.LocalDate;
import java.util.List;

public class TeamActivitiesResponse {

    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<TeamActivitiesGroupResponse> groups;

    public TeamActivitiesResponse(LocalDate weekStart, LocalDate weekEnd, List<TeamActivitiesGroupResponse> groups) {
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.groups = groups;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public LocalDate getWeekEnd() {
        return weekEnd;
    }

    public List<TeamActivitiesGroupResponse> getGroups() {
        return groups;
    }
}
