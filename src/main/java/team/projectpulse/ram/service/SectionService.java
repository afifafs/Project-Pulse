package team.projectpulse.ram.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.CriterionResponse;
import team.projectpulse.ram.dto.RubricDetailResponse;
import team.projectpulse.ram.dto.SectionRequest;
import team.projectpulse.ram.dto.SectionDetailResponse;
import team.projectpulse.ram.dto.StudentResponse;
import team.projectpulse.ram.dto.TeamDetailResponse;
import team.projectpulse.ram.exception.DuplicateResourceException;
import team.projectpulse.ram.exception.InvalidSectionRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Rubric;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Team;
import team.projectpulse.ram.repository.RubricRepository;
import team.projectpulse.ram.repository.SectionRepository;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final RubricRepository rubricRepository;

    public SectionService(SectionRepository sectionRepository, RubricRepository rubricRepository) {
        this.sectionRepository = sectionRepository;
        this.rubricRepository = rubricRepository;
    }

    @Transactional
    public SectionDetailResponse createSection(SectionRequest request) {
        validateSectionRequest(request);

        Rubric rubric = rubricRepository.findById(request.getRubricId())
                .orElseThrow(() -> new ResourceNotFoundException("Rubric not found with id: " + request.getRubricId()));

        Section section = new Section();
        section.setName(request.getName().trim());
        section.setStartDate(request.getStartDate());
        section.setEndDate(request.getEndDate());
        section.setRubric(rubric);

        Section createdSection = sectionRepository.save(section);
        return mapToSectionDetailResponse(createdSection);
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

    private void validateSectionRequest(SectionRequest request) {
        if (request == null) {
            throw new InvalidSectionRequestException("Section request is required.");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidSectionRequestException("Section name is required.");
        }

        if (sectionRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("A section with this name already exists.");
        }

        if (request.getStartDate() == null) {
            throw new InvalidSectionRequestException("Section startDate is required.");
        }

        if (request.getEndDate() == null) {
            throw new InvalidSectionRequestException("Section endDate is required.");
        }

        if (request.getRubricId() == null) {
            throw new InvalidSectionRequestException("rubricId is required.");
        }
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
