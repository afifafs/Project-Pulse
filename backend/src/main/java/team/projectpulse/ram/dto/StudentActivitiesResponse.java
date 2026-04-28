package team.projectpulse.ram.dto;

import java.time.LocalDate;
import java.util.List;

public class StudentActivitiesResponse {

    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<ActivityEntryResponse> rows;

    public StudentActivitiesResponse(LocalDate weekStart, LocalDate weekEnd, List<ActivityEntryResponse> rows) {
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.rows = rows;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public LocalDate getWeekEnd() {
        return weekEnd;
    }

    public List<ActivityEntryResponse> getRows() {
        return rows;
    }
}
