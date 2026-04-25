package team.projectpulse.ram.dto;

import team.projectpulse.ram.model.PeerEvaluationRating;

public class PeerEvaluationRatingResponse {

    private Long criterionId;
    private String criterionName;
    private Double score;
    private Double maxScore;

    public PeerEvaluationRatingResponse(Long criterionId, String criterionName, Double score, Double maxScore) {
        this.criterionId = criterionId;
        this.criterionName = criterionName;
        this.score = score;
        this.maxScore = maxScore;
    }

    public static PeerEvaluationRatingResponse fromEntity(PeerEvaluationRating rating) {
        return new PeerEvaluationRatingResponse(
                rating.getCriterionId(),
                rating.getCriterionName(),
                rating.getScore(),
                rating.getMaxScore()
        );
    }

    public Long getCriterionId() {
        return criterionId;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public Double getScore() {
        return score;
    }

    public Double getMaxScore() {
        return maxScore;
    }
}
