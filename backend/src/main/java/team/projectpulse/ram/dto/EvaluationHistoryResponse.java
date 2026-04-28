package team.projectpulse.ram.dto;

import java.util.List;

public class EvaluationHistoryResponse {

    private List<EvaluationWeekSummaryResponse> rows;

    public EvaluationHistoryResponse(List<EvaluationWeekSummaryResponse> rows) {
        this.rows = rows;
    }

    public List<EvaluationWeekSummaryResponse> getRows() {
        return rows;
    }
}
