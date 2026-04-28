package team.projectpulse.ram.dto;

import java.time.LocalDate;
import java.util.List;

public class StudentEvaluationWeekDetailResponse {

    private LocalDate weekStart;
    private LocalDate weekEnd;
    private Double averageTotalScore;
    private List<Double> criterionScores;
    private List<String> publicComments;
    private List<String> privateComments;

    public StudentEvaluationWeekDetailResponse(
            LocalDate weekStart,
            LocalDate weekEnd,
            Double averageTotalScore,
            List<Double> criterionScores,
            List<String> publicComments,
            List<String> privateComments
    ) {
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.averageTotalScore = averageTotalScore;
        this.criterionScores = criterionScores;
        this.publicComments = publicComments;
        this.privateComments = privateComments;
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

    public List<String> getPublicComments() {
        return publicComments;
    }

    public List<String> getPrivateComments() {
        return privateComments;
    }
}
