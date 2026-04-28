package team.projectpulse.ram.dto;

import java.util.ArrayList;
import java.util.List;

public class PeerEvaluationSubmissionRequest {

    private Long revieweeId;
    private String publicComment;
    private List<PeerEvaluationScoreRequest> scores = new ArrayList<>();

    public Long getRevieweeId() {
        return revieweeId;
    }

    public void setRevieweeId(Long revieweeId) {
        this.revieweeId = revieweeId;
    }

    public String getPublicComment() {
        return publicComment;
    }

    public void setPublicComment(String publicComment) {
        this.publicComment = publicComment;
    }

    public List<PeerEvaluationScoreRequest> getScores() {
        return scores;
    }

    public void setScores(List<PeerEvaluationScoreRequest> scores) {
        this.scores = scores;
    }
}
