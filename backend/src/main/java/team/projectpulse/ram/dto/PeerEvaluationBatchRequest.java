package team.projectpulse.ram.dto;

import java.util.ArrayList;
import java.util.List;

public class PeerEvaluationBatchRequest {

    private List<PeerEvaluationSubmissionRequest> submissions = new ArrayList<>();

    public List<PeerEvaluationSubmissionRequest> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<PeerEvaluationSubmissionRequest> submissions) {
        this.submissions = submissions;
    }
}
