package team.projectpulse.ram.dto;

import team.projectpulse.ram.model.Criterion;

public class CriterionResponse {

    private Long id;

    private String name;

    private String description;

    private Integer maxScore;

    public CriterionResponse(Long id, String name, String description, Integer maxScore) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxScore = maxScore;
    }

    public static CriterionResponse fromEntity(Criterion criterion) {
        return new CriterionResponse(
                criterion.getId(),
                criterion.getName(),
                criterion.getDescription(),
                criterion.getMaxScore()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMaxScore() {
        return maxScore;
    }
}
