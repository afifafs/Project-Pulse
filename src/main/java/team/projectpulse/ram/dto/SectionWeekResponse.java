package team.projectpulse.ram.dto;

import java.time.LocalDate;
import team.projectpulse.ram.model.SectionWeek;

public class SectionWeekResponse {

    private Long id;

    private Integer weekNumber;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;

    public SectionWeekResponse(Long id, Integer weekNumber, LocalDate startDate, LocalDate endDate, Boolean active) {
        this.id = id;
        this.weekNumber = weekNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public static SectionWeekResponse fromEntity(SectionWeek week) {
        return new SectionWeekResponse(
                week.getId(),
                week.getWeekNumber(),
                week.getStartDate(),
                week.getEndDate(),
                week.getActive()
        );
    }

    public Long getId() {
        return id;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Boolean getActive() {
        return active;
    }
}
