package team.projectpulse.ram.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import team.projectpulse.ram.model.Criterion;
import team.projectpulse.ram.repository.CriterionRepository;

@Service
public class CriterionService {

    private final CriterionRepository criterionRepository;

    public CriterionService(CriterionRepository criterionRepository) {
        this.criterionRepository = criterionRepository;
    }

    public Criterion createCriterion(Criterion criterion) {
        return null;
    }

    public Optional<Criterion> getCriterionById(Long id) {
        return Optional.empty();
    }

    public List<Criterion> getAllCriteria() {
        return List.of();
    }

    public Criterion updateCriterion(Long id, Criterion criterion) {
        return null;
    }

    public void deleteCriterion(Long id) {
    }
}
