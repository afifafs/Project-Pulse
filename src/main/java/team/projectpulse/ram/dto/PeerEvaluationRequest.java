package team.projectpulse.ram.dto;

import java.util.ArrayList;
import java.util.List;

public class PeerEvaluationRequest {

    private Long evaluatorId;
    private Long evaluateeId;
    private Integer weekNumber;
    private String publicComment;
    private String privateComment;
    private List<PeerEvaluationRatingRequest> ratings = new ArrayList<>();

    public Long getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(Long evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public Long getEvaluateeId() {
        return evaluateeId;
    }

    public void setEvaluateeId(Long evaluateeId) {
        this.evaluateeId = evaluateeId;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getPublicComment() {
        return publicComment;
    }

    public void setPublicComment(String publicComment) {
        this.publicComment = publicComment;
    }

    public String getPrivateComment() {
        return privateComment;
    }

    public void setPrivateComment(String privateComment) {
        this.privateComment = privateComment;
    }

    public List<PeerEvaluationRatingRequest> getRatings() {
        return ratings;
    }

    public void setRatings(List<PeerEvaluationRatingRequest> ratings) {
        this.ratings = ratings;
    }
}
