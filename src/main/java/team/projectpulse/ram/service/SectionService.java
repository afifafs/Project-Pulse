package team.projectpulse.ram.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.ActiveWeeksRequest;
import team.projectpulse.ram.dto.CriterionResponse;
import team.projectpulse.ram.dto.RubricDetailResponse;
import team.projectpulse.ram.dto.SectionRequest;
import team.projectpulse.ram.dto.SectionDetailResponse;
import team.projectpulse.ram.dto.SectionWeekResponse;
import team.projectpulse.ram.dto.StudentResponse;
import team.projectpulse.ram.dto.TeamDetailResponse;
import team.projectpulse.ram.exception.DuplicateResourceException;
import team.projectpulse.ram.exception.InvalidSectionRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Rubric;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.SectionWeek;
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

    @Transactional
    public SectionDetailResponse setActiveWeeks(Long id, ActiveWeeksRequest request) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));

        validateSectionDateRange(section);

        List<WeekRange> weekRanges = generateWeekRanges(section.getStartDate(), section.getEndDate());
        Set<Integer> inactiveWeekNumbers = resolveInactiveWeekNumbers(request, weekRanges);

        section.getWeeks().clear();
        for (WeekRange weekRange : weekRanges) {
            boolean active = !inactiveWeekNumbers.contains(weekRange.weekNumber());
            section.getWeeks().add(new SectionWeek(
                    weekRange.weekNumber(),
                    weekRange.startDate(),
                    weekRange.endDate(),
                    active,
                    section
            ));
        }

        Section updatedSection = sectionRepository.save(section);
        return mapToSectionDetailResponse(updatedSection);
    }

    @Transactional
    public SectionDetailResponse updateSection(Long id, SectionRequest request) {
        validateSectionRequest(request);

        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));

        String updatedName = request.getName().trim();
        if (!section.getName().equalsIgnoreCase(updatedName)
                && sectionRepository.existsByNameIgnoreCaseAndIdNot(updatedName, id)) {
            throw new DuplicateResourceException("A section with this name already exists.");
        }

        Rubric rubric = rubricRepository.findById(request.getRubricId())
                .orElseThrow(() -> new ResourceNotFoundException("Rubric not found with id: " + request.getRubricId()));

        section.setName(updatedName);
        section.setStartDate(request.getStartDate());
        section.setEndDate(request.getEndDate());
        section.setRubric(rubric);

        Section updatedSection = sectionRepository.save(section);
        return mapToSectionDetailResponse(updatedSection);
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

    private void validateSectionDateRange(Section section) {
        if (section.getStartDate() == null || section.getEndDate() == null) {
            throw new InvalidSectionRequestException("Section startDate and endDate are required to set active weeks.");
        }

        if (section.getEndDate().isBefore(section.getStartDate())) {
            throw new InvalidSectionRequestException("Section endDate cannot be before startDate.");
        }
    }

    private Set<Integer> resolveInactiveWeekNumbers(ActiveWeeksRequest request, List<WeekRange> weekRanges) {
        Set<Integer> inactiveWeekNumbers = new HashSet<>();
        List<String> inactiveWeeks = request == null || request.getInactiveWeeks() == null
                ? List.of()
                : request.getInactiveWeeks();

        for (String inactiveWeek : inactiveWeeks) {
            if (inactiveWeek == null || inactiveWeek.trim().isEmpty()) {
                throw new InvalidSectionRequestException("Inactive week values cannot be blank.");
            }

            inactiveWeekNumbers.add(resolveInactiveWeekNumber(inactiveWeek.trim(), weekRanges));
        }

        return inactiveWeekNumbers;
    }

    private Integer resolveInactiveWeekNumber(String inactiveWeek, List<WeekRange> weekRanges) {
        if (inactiveWeek.chars().allMatch(Character::isDigit)) {
            int weekNumber = Integer.parseInt(inactiveWeek);
            if (weekRanges.stream().anyMatch(weekRange -> weekRange.weekNumber() == weekNumber)) {
                return weekNumber;
            }

            throw new InvalidSectionRequestException("Inactive week ID is outside the section range: " + inactiveWeek);
        }

        try {
            LocalDate inactiveDate = LocalDate.parse(inactiveWeek);
            return weekRanges.stream()
                    .filter(weekRange -> !inactiveDate.isBefore(weekRange.startDate())
                            && !inactiveDate.isAfter(weekRange.endDate()))
                    .findFirst()
                    .map(WeekRange::weekNumber)
                    .orElseThrow(() -> new InvalidSectionRequestException(
                            "Inactive week date is outside the section range: " + inactiveWeek
                    ));
        } catch (DateTimeParseException exception) {
            throw new InvalidSectionRequestException(
                    "Inactive weeks must be week IDs or ISO dates like 2026-09-07."
            );
        }
    }

    private List<WeekRange> generateWeekRanges(LocalDate startDate, LocalDate endDate) {
        List<WeekRange> weekRanges = new java.util.ArrayList<>();
        LocalDate weekStart = startDate;
        int weekNumber = 1;

        while (!weekStart.isAfter(endDate)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(endDate)) {
                weekEnd = endDate;
            }

            weekRanges.add(new WeekRange(weekNumber, weekStart, weekEnd));
            weekStart = weekEnd.plusDays(1);
            weekNumber++;
        }

        return weekRanges;
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
                mapToRubricDetailResponse(section.getRubric()),
                section.getWeeks().stream()
                        .filter(week -> Boolean.TRUE.equals(week.getActive()))
                        .map(SectionWeekResponse::fromEntity)
                        .toList()
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

    private record WeekRange(Integer weekNumber, LocalDate startDate, LocalDate endDate) {
    }
}
