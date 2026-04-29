package team.projectpulse.ram.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.CriterionRequest;
import team.projectpulse.ram.dto.CriterionResponse;
import team.projectpulse.ram.dto.RubricDetailResponse;
import team.projectpulse.ram.dto.RubricRequest;
import team.projectpulse.ram.exception.DuplicateResourceException;
import team.projectpulse.ram.exception.InvalidRubricRequestException;
import team.projectpulse.ram.model.Criterion;
import team.projectpulse.ram.model.Rubric;
import team.projectpulse.ram.repository.RubricRepository;

@Service
public class RubricService {

    private final RubricRepository rubricRepository;

    public RubricService(RubricRepository rubricRepository) {
        this.rubricRepository = rubricRepository;
    }

    @Transactional
    public RubricDetailResponse createRubric(RubricRequest dto) {
        validateRubricRequest(dto);

        Rubric rubric = new Rubric();
        rubric.setName(dto.getName().trim());

        for (CriterionRequest criterionRequest : dto.getCriteria()) {
            Criterion criterion = new Criterion();
            criterion.setName(criterionRequest.getName().trim());
            criterion.setDescription(criterionRequest.getDescription());
            criterion.setMaxScore(criterionRequest.getMaxScore());
            rubric.addCriterion(criterion);
        }

        return map(rubricRepository.save(rubric));
    }

    public Optional<Rubric> getRubricById(Long id) {
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<RubricDetailResponse> getAllRubrics() {
        return rubricRepository.findAllWithCriteria().stream()
                .map(this::map)
                .toList();
    }

    public Rubric updateRubric(Long id, Rubric rubric) {
        return null;
    }

    public void deleteRubric(Long id) {
    }

    private void validateRubricRequest(RubricRequest dto) {
        if (dto == null) {
            throw new InvalidRubricRequestException("Rubric request is required.");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidRubricRequestException("Rubric name is required.");
        }

        if (rubricRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new DuplicateResourceException("A rubric with this name already exists.");
        }

        if (dto.getCriteria() == null || dto.getCriteria().isEmpty()) {
            throw new InvalidRubricRequestException("At least one criterion is required.");
        }

        for (CriterionRequest criterion : dto.getCriteria()) {
            validateCriterionRequest(criterion);
        }
    }

    private void validateCriterionRequest(CriterionRequest criterion) {
        if (criterion == null) {
            throw new InvalidRubricRequestException("Criterion is required.");
        }

        if (criterion.getName() == null || criterion.getName().trim().isEmpty()) {
            throw new InvalidRubricRequestException("Criterion name is required.");
        }

        if (criterion.getMaxScore() == null || criterion.getMaxScore() <= 0) {
            throw new InvalidRubricRequestException("Criterion maxScore must be greater than 0.");
        }
    }

    private RubricDetailResponse map(Rubric rubric) {
        List<CriterionResponse> criteria = rubric.getCriteria().stream()
                .map(CriterionResponse::fromEntity)
                .toList();

        return new RubricDetailResponse(
                rubric.getId(),
                rubric.getName(),
                rubric.getDescription(),
                criteria
        );
    }
}
