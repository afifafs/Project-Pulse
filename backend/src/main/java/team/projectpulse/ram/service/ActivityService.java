package team.projectpulse.ram.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.ActivityEntryRequest;
import team.projectpulse.ram.dto.ActivityEntryResponse;
import team.projectpulse.ram.dto.StudentActivitiesResponse;
import team.projectpulse.ram.dto.TeamActivitiesGroupResponse;
import team.projectpulse.ram.dto.TeamActivitiesResponse;
import team.projectpulse.ram.exception.InvalidStudentRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.ActivityEntry;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.repository.ActivityEntryRepository;
import team.projectpulse.ram.repository.SectionRepository;
import team.projectpulse.ram.repository.StudentRepository;

@Service
public class ActivityService {

    private final ActivityEntryRepository activityEntryRepository;
    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;

    public ActivityService(
            ActivityEntryRepository activityEntryRepository,
            StudentRepository studentRepository,
            SectionRepository sectionRepository
    ) {
        this.activityEntryRepository = activityEntryRepository;
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public StudentActivitiesResponse getStudentActivities(Long studentId, LocalDate weekStart) {
        Student student = studentRepository.findByIdWithDetails(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        LocalDate normalizedWeekStart = validateWeekInSection(student.getSection(), weekStart);

        List<ActivityEntryResponse> rows = activityEntryRepository
                .findAllByStudentIdAndWeekStartOrderByIdAsc(studentId, normalizedWeekStart)
                .stream()
                .map(ActivityEntryResponse::fromEntity)
                .toList();

        return new StudentActivitiesResponse(normalizedWeekStart, normalizedWeekStart.plusDays(6), rows);
    }

    @Transactional
    public StudentActivitiesResponse saveStudentActivities(Long studentId, LocalDate weekStart, List<ActivityEntryRequest> requests) {
        Student student = studentRepository.findByIdWithDetails(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        LocalDate normalizedWeekStart = validateWeekInSection(student.getSection(), weekStart);

        List<ActivityEntryRequest> safeRequests = requests == null ? List.of() : requests;
        activityEntryRepository.deleteAllByStudentIdAndWeekStart(studentId, normalizedWeekStart);

        List<ActivityEntry> savedEntries = new ArrayList<>();
        for (ActivityEntryRequest request : safeRequests) {
            validateActivityRequest(request);

            ActivityEntry entry = new ActivityEntry();
            entry.setWeekStart(normalizedWeekStart);
            entry.setCategory(request.getCategory().trim());
            entry.setActivity(request.getActivity().trim());
            entry.setDescription(request.getDescription().trim());
            entry.setPlannedHours(request.getPlannedHours());
            entry.setActualHours(request.getActualHours());
            entry.setStatus(request.getStatus().trim());
            entry.setStudent(student);
            entry.setSection(student.getSection());
            savedEntries.add(entry);
        }

        List<ActivityEntryResponse> rows = activityEntryRepository.saveAll(savedEntries).stream()
                .map(ActivityEntryResponse::fromEntity)
                .toList();

        return new StudentActivitiesResponse(normalizedWeekStart, normalizedWeekStart.plusDays(6), rows);
    }

    @Transactional(readOnly = true)
    public TeamActivitiesResponse getSectionActivities(Long sectionId, LocalDate weekStart) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        LocalDate normalizedWeekStart = validateWeekInSection(section, weekStart);

        List<Student> students = studentRepository.findAllBySectionIdWithDetails(sectionId);
        List<ActivityEntry> entries = activityEntryRepository
                .findAllBySectionIdAndWeekStartOrderByStudentLastNameAscStudentFirstNameAscIdAsc(sectionId, normalizedWeekStart);

        Map<Long, List<ActivityEntryResponse>> rowsByStudent = new LinkedHashMap<>();
        for (Student student : students) {
            rowsByStudent.put(student.getId(), new ArrayList<>());
        }
        for (ActivityEntry entry : entries) {
            rowsByStudent.computeIfAbsent(entry.getStudent().getId(), ignored -> new ArrayList<>())
                    .add(ActivityEntryResponse.fromEntity(entry));
        }

        List<TeamActivitiesGroupResponse> groups = students.stream()
                .map(student -> new TeamActivitiesGroupResponse(
                        student.getId(),
                        student.getFirstName() + " " + student.getLastName(),
                        rowsByStudent.getOrDefault(student.getId(), List.of())
                ))
                .toList();

        return new TeamActivitiesResponse(normalizedWeekStart, normalizedWeekStart.plusDays(6), groups);
    }

    private LocalDate validateWeekInSection(Section section, LocalDate weekStart) {
        if (section == null) {
            throw new InvalidStudentRequestException("Student must belong to a section.");
        }
        if (weekStart == null) {
            throw new InvalidStudentRequestException("weekStart is required.");
        }
        if (section.getStartDate() == null || section.getEndDate() == null) {
            throw new InvalidStudentRequestException("Section week range is not configured.");
        }
        if (weekStart.isBefore(section.getStartDate()) || weekStart.isAfter(section.getEndDate())) {
            throw new InvalidStudentRequestException("Requested week is outside the section range.");
        }
        return weekStart;
    }

    private void validateActivityRequest(ActivityEntryRequest request) {
        if (request == null) {
            throw new InvalidStudentRequestException("Activity rows cannot be null.");
        }
        if (isBlank(request.getCategory()) || isBlank(request.getActivity()) || isBlank(request.getDescription())
                || isBlank(request.getStatus())) {
            throw new InvalidStudentRequestException("Category, activity, description, and status are required.");
        }
        if (request.getPlannedHours() == null || request.getPlannedHours() < 0
                || request.getActualHours() == null || request.getActualHours() < 0) {
            throw new InvalidStudentRequestException("Planned and actual hours must be 0 or greater.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
