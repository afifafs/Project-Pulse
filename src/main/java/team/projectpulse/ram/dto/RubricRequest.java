package team.projectpulse.ram.dto;

import java.util.ArrayList;
import java.util.List;

public class RubricRequest {

    private String name;

    private List<CriterionRequest> criteria = new ArrayList<>();

    public RubricRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CriterionRequest> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<CriterionRequest> criteria) {
        this.criteria = criteria;
    }
}
