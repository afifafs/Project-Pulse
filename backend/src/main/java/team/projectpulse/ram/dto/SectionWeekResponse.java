package team.projectpulse.ram.dto;

import java.time.LocalDate;
import team.projectpulse.ram.model.SectionWeek;

public class SectionWeekResponse {

    private Long id;
    private Integer weekNumber;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private boolean active;

    public SectionWeekResponse(Long id, Integer weekNumber, LocalDate weekStart, LocalDate weekEnd, boolean active) {
        this.id = id;
        this.weekNumber = weekNumber;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.active = active;
    }

    public static SectionWeekResponse fromEntity(SectionWeek week) {
        return new SectionWeekResponse(
                week.getId(),
                week.getWeekNumber(),
                week.getWeekStart(),
                week.getWeekEnd(),
                week.isActive()
        );
    }

    public Long getId() {
        return id;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public LocalDate getWeekEnd() {
        return weekEnd;
    }

    public boolean isActive() {
        return active;
    }
}
