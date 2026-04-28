package team.projectpulse.ram.dto;

import java.time.LocalDate;
import java.util.List;

public class EvaluationWeekSummaryResponse {

    private LocalDate weekStart;
    private LocalDate weekEnd;
    private Double averageTotalScore;
    private List<Double> criterionScores;

    public EvaluationWeekSummaryResponse(
            LocalDate weekStart,
            LocalDate weekEnd,
            Double averageTotalScore,
            List<Double> criterionScores
    ) {
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.averageTotalScore = averageTotalScore;
        this.criterionScores = criterionScores;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public LocalDate getWeekEnd() {
        return weekEnd;
    }

    public Double getAverageTotalScore() {
        return averageTotalScore;
    }

    public List<Double> getCriterionScores() {
        return criterionScores;
    }
}
