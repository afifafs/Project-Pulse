package team.projectpulse.ram.service;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.CriterionResponse;
import team.projectpulse.ram.dto.ActiveWeeksRequest;
import team.projectpulse.ram.dto.InstructorResponse;
import team.projectpulse.ram.dto.RubricDetailResponse;
import team.projectpulse.ram.dto.SectionDetailResponse;
import team.projectpulse.ram.dto.SectionRequest;
import team.projectpulse.ram.dto.SectionWeekResponse;
import team.projectpulse.ram.dto.StudentResponse;
import team.projectpulse.ram.dto.TeamDetailResponse;
import team.projectpulse.ram.exception.DuplicateResourceException;
import team.projectpulse.ram.exception.InvalidStudentRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Instructor;
import team.projectpulse.ram.model.Rubric;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.SectionWeek;
import team.projectpulse.ram.model.Team;
import team.projectpulse.ram.repository.RubricRepository;
import team.projectpulse.ram.repository.SectionRepository;
import team.projectpulse.ram.repository.SectionWeekRepository;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final RubricRepository rubricRepository;
    private final SectionWeekRepository sectionWeekRepository;

    public SectionService(
            SectionRepository sectionRepository,
            RubricRepository rubricRepository,
            SectionWeekRepository sectionWeekRepository
    ) {
        this.sectionRepository = sectionRepository;
        this.rubricRepository = rubricRepository;
        this.sectionWeekRepository = sectionWeekRepository;
    }

    @Transactional
    public SectionDetailResponse createSection(SectionRequest request) {
        validateSectionRequest(request, null);

        Section section = new Section();
        applySectionUpdates(section, request);
        sectionRepository.save(section);
        regenerateWeeks(section, Set.of());
        return getSectionDetails(section.getId());
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
        Section section = sectionRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));

        return mapToSectionDetailResponse(section);
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAllByOrderByNameDesc();
    }

    @Transactional
    public SectionDetailResponse updateSection(Long id, SectionRequest request) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));
        validateSectionRequest(request, id);

        applySectionUpdates(section, request);
        sectionRepository.save(section);
        if (section.getWeeks().isEmpty()) {
            regenerateWeeks(section, Set.of());
        } else {
            Set<LocalDate> inactiveWeeks = section.getWeeks().stream()
                    .filter(week -> !week.isActive())
                    .map(SectionWeek::getWeekStart)
                    .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
            regenerateWeeks(section, inactiveWeeks);
        }
        return getSectionDetails(id);
    }

    public void deleteSection(Long id) {
        if (!sectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Section not found with id: " + id);
        }
        sectionRepository.deleteById(id);
    }

    @Transactional
    public SectionDetailResponse updateActiveWeeks(Long id, ActiveWeeksRequest request) {
        Section section = sectionRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));

        Set<LocalDate> inactiveWeeks = request == null || request.getInactiveWeeks() == null
                ? Set.of()
                : new LinkedHashSet<>(request.getInactiveWeeks());

        validateInactiveWeeks(section, inactiveWeeks);
        regenerateWeeks(section, inactiveWeeks);
        return getSectionDetails(id);
    }

    private SectionDetailResponse mapToSectionDetailResponse(Section section) {
        List<TeamDetailResponse> teams = section.getTeams().stream()
                .map(this::mapToTeamDetailResponse)
                .toList();

        List<StudentResponse> unassignedStudents = section.getStudents().stream()
                .filter(student -> student.getTeam() == null)
                .map(StudentResponse::fromEntity)
                .toList();

        List<String> instructorNames = section.getInstructors().stream()
                .map(this::fullName)
                .sorted()
                .toList();

        List<SectionWeekResponse> weeks = section.getWeeks().stream()
                .sorted(java.util.Comparator.comparing(SectionWeek::getWeekStart))
                .map(SectionWeekResponse::fromEntity)
                .toList();

        return new SectionDetailResponse(
                section.getId(),
                section.getName(),
                section.getCourseCode(),
                section.getStartDate(),
                section.getEndDate(),
                teams,
                unassignedStudents,
                instructorNames,
                mapToRubricDetailResponse(section.getRubric()),
                weeks
        );
    }

    private TeamDetailResponse mapToTeamDetailResponse(Team team) {
        List<StudentResponse> students = team.getStudents().stream()
                .map(StudentResponse::fromEntity)
                .toList();

        List<InstructorResponse> instructors = team.getInstructors().stream()
                .map(InstructorResponse::fromEntity)
                .sorted(java.util.Comparator.comparing(InstructorResponse::getLastName,
                        java.util.Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();

        return new TeamDetailResponse(
                team.getId(),
                team.getName(),
                team.getSection() == null ? null : team.getSection().getName(),
                team.getDescription(),
                team.getWebsite(),
                students,
                instructors
        );
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

    private void validateSectionRequest(SectionRequest request, Long existingId) {
        if (request == null) {
            throw new InvalidStudentRequestException("Section request is required.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidStudentRequestException("Section name is required.");
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new InvalidStudentRequestException("startDate and endDate are required.");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidStudentRequestException("endDate cannot be before startDate.");
        }
        if (request.getRubricId() == null) {
            throw new InvalidStudentRequestException("rubricId is required.");
        }

        boolean nameExists = existingId == null
                ? sectionRepository.existsByNameIgnoreCase(request.getName().trim())
                : sectionRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), existingId);
        if (nameExists) {
            throw new DuplicateResourceException("A section with this name already exists.");
        }
        if (!rubricRepository.existsById(request.getRubricId())) {
            throw new ResourceNotFoundException("Rubric not found with id: " + request.getRubricId());
        }
    }

    private void applySectionUpdates(Section section, SectionRequest request) {
        Rubric rubric = rubricRepository.findById(request.getRubricId())
                .orElseThrow(() -> new ResourceNotFoundException("Rubric not found with id: " + request.getRubricId()));
        section.setName(request.getName().trim());
        section.setCourseCode(request.getCourseCode() == null ? null : request.getCourseCode().trim());
        section.setStartDate(request.getStartDate());
        section.setEndDate(request.getEndDate());
        section.setRubric(rubric);
    }

    private void validateInactiveWeeks(Section section, Set<LocalDate> inactiveWeeks) {
        if (section.getStartDate() == null || section.getEndDate() == null) {
            throw new InvalidStudentRequestException("Section date range must be configured first.");
        }
        for (LocalDate weekStart : inactiveWeeks) {
            if (weekStart == null) {
                throw new InvalidStudentRequestException("Inactive week values cannot be null.");
            }
            if (weekStart.isBefore(section.getStartDate()) || weekStart.isAfter(section.getEndDate())) {
                throw new InvalidStudentRequestException("Inactive week " + weekStart + " falls outside the section range.");
            }
        }
    }

    private void regenerateWeeks(Section section, Set<LocalDate> inactiveWeeks) {
        sectionWeekRepository.deleteAllBySectionId(section.getId());

        List<SectionWeek> generated = new java.util.ArrayList<>();
        LocalDate cursor = section.getStartDate();
        int weekNumber = 1;
        while (!cursor.isAfter(section.getEndDate())) {
            SectionWeek week = new SectionWeek();
            week.setSection(section);
            week.setWeekNumber(weekNumber);
            week.setWeekStart(cursor);
            LocalDate weekEnd = cursor.plusDays(6);
            if (weekEnd.isAfter(section.getEndDate())) {
                weekEnd = section.getEndDate();
            }
            week.setWeekEnd(weekEnd);
            week.setActive(!inactiveWeeks.contains(cursor));
            generated.add(week);
            cursor = cursor.plusWeeks(1);
            weekNumber += 1;
        }

        section.setWeeks(sectionWeekRepository.saveAll(generated));
    }

    private String fullName(Instructor instructor) {
        String firstName = instructor.getFirstName() == null ? "" : instructor.getFirstName().trim();
        String lastName = instructor.getLastName() == null ? "" : instructor.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isEmpty() ? instructor.getEmail() : fullName;
    }
}
