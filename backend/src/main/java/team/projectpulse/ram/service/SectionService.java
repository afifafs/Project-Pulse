package team.projectpulse.ram.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.CriterionResponse;
import team.projectpulse.ram.dto.RubricDetailResponse;
import team.projectpulse.ram.dto.SectionDetailResponse;
import team.projectpulse.ram.dto.StudentResponse;
import team.projectpulse.ram.dto.TeamDetailResponse;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Rubric;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Team;
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

    @Transactional(readOnly = true)
    public SectionDetailResponse getSectionDetails(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));

        return mapToSectionDetailResponse(section);
    }

    public List<Section> getAllSections() {
        return List.of();
    }

    public Section updateSection(Long id, Section section) {
        return null;
    }

    public void deleteSection(Long id) {
    }

    private SectionDetailResponse mapToSectionDetailResponse(Section section) {
        List<TeamDetailResponse> teams = section.getTeams().stream()
                .map(this::mapToTeamDetailResponse)
                .toList();

        List<StudentResponse> unassignedStudents = section.getStudents().stream()
                .filter(student -> student.getTeam() == null)
                .map(StudentResponse::fromEntity)
                .toList();

        return new SectionDetailResponse(
                section.getId(),
                section.getName(),
                section.getCourseCode(),
                section.getStartDate(),
                section.getEndDate(),
                teams,
                unassignedStudents,
                List.copyOf(section.getInstructors()),
                mapToRubricDetailResponse(section.getRubric())
        );
    }

    private TeamDetailResponse mapToTeamDetailResponse(Team team) {
        List<StudentResponse> students = team.getStudents().stream()
                .map(StudentResponse::fromEntity)
                .toList();

        return new TeamDetailResponse(team.getId(), team.getName(), students);
    }

    private RubricDetailResponse mapToRubricDetailResponse(Rubric rubric) {
        if (rubric == null) {
            return null;
        }

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
