package team.projectpulse.ram.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.repository.SectionRepository;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section createSection(Section section) {
        return null;
    }

    public Optional<Section> getSectionById(Long id) {
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<Section> findSections(String name) {
        if (name == null || name.trim().isEmpty()) {
            return sectionRepository.findAllByOrderByNameDesc();
        }

        return sectionRepository.findByNameContainingIgnoreCaseOrderByNameDesc(name.trim());
    }

    public List<Section> getAllSections() {
        return List.of();
    }

    public Section updateSection(Long id, Section section) {
        return null;
    }

    public void deleteSection(Long id) {
    }
}
