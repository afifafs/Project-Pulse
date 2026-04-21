package team.projectpulse.ram.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
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

    public List<Section> getAllSections() {
        return List.of();
    }

    public Section updateSection(Long id, Section section) {
        return null;
    }

    public void deleteSection(Long id) {
    }
}
