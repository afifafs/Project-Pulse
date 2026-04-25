package team.projectpulse.ram.dto;

public class PeerEvaluationRatingRequest {

    private Long criterionId;
    private Double score;

    public Long getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(Long criterionId) {
        this.criterionId = criterionId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
