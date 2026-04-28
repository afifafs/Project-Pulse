package team.projectpulse.ram.dto;

import java.time.LocalDate;
import java.util.List;

public class SectionEvaluationReportResponse {

    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<SectionEvaluationStudentSummaryResponse> rows;

    public SectionEvaluationReportResponse(
            LocalDate weekStart,
            LocalDate weekEnd,
            List<SectionEvaluationStudentSummaryResponse> rows
    ) {
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

    public List<SectionEvaluationStudentSummaryResponse> getRows() {
        return rows;
    }
}
