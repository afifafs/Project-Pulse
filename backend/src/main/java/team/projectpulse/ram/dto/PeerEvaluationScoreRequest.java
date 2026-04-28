package team.projectpulse.ram.dto;

public class PeerEvaluationScoreRequest {

    private Long criterionId;
    private Integer score;

    public Long getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(Long criterionId) {
        this.criterionId = criterionId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
