package team.projectpulse.ram.dto;

import java.time.LocalDateTime;
import java.util.List;
import team.projectpulse.ram.model.PeerEvaluation;

public class PeerEvaluationResponse {

    private Long id;
    private Integer weekNumber;
    private Long evaluatorId;
    private String evaluatorName;
    private Long evaluateeId;
    private String evaluateeName;
    private Double totalScore;
    private String publicComment;
    private String privateComment;
    private LocalDateTime submittedAt;
    private List<PeerEvaluationRatingResponse> ratings;

    public PeerEvaluationResponse(
            Long id,
            Integer weekNumber,
            Long evaluatorId,
            String evaluatorName,
            Long evaluateeId,
            String evaluateeName,
            Double totalScore,
            String publicComment,
            String privateComment,
            LocalDateTime submittedAt,
            List<PeerEvaluationRatingResponse> ratings
    ) {
        this.id = id;
        this.weekNumber = weekNumber;
        this.evaluatorId = evaluatorId;
        this.evaluatorName = evaluatorName;
        this.evaluateeId = evaluateeId;
        this.evaluateeName = evaluateeName;
        this.totalScore = totalScore;
        this.publicComment = publicComment;
        this.privateComment = privateComment;
        this.submittedAt = submittedAt;
        this.ratings = ratings;
    }

    public static PeerEvaluationResponse fromEntity(PeerEvaluation evaluation, boolean includePrivateComment) {
        String evaluatorName = evaluation.getEvaluator().getFirstName() + " " + evaluation.getEvaluator().getLastName();
        String evaluateeName = evaluation.getEvaluatee().getFirstName() + " " + evaluation.getEvaluatee().getLastName();

        return new PeerEvaluationResponse(
                evaluation.getId(),
                evaluation.getWeekNumber(),
                evaluation.getEvaluator().getId(),
                evaluatorName,
                evaluation.getEvaluatee().getId(),
                evaluateeName,
                evaluation.getTotalScore(),
                evaluation.getPublicComment(),
                includePrivateComment ? evaluation.getPrivateComment() : null,
                evaluation.getSubmittedAt(),
                evaluation.getRatings().stream().map(PeerEvaluationRatingResponse::fromEntity).toList()
        );
    }

    public Long getId() {
        return id;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public Long getEvaluatorId() {
        return evaluatorId;
    }

    public String getEvaluatorName() {
        return evaluatorName;
    }

    public Long getEvaluateeId() {
        return evaluateeId;
    }

    public String getEvaluateeName() {
        return evaluateeName;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public String getPublicComment() {
        return publicComment;
    }

    public String getPrivateComment() {
        return privateComment;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public List<PeerEvaluationRatingResponse> getRatings() {
        return ratings;
    }
}
