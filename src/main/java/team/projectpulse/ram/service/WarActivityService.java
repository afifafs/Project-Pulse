package team.projectpulse.ram.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.StudentWarReportResponse;
import team.projectpulse.ram.dto.TeamWarReportResponse;
import team.projectpulse.ram.dto.TeamWarStudentSummary;
import team.projectpulse.ram.dto.WarActivityRequest;
import team.projectpulse.ram.dto.WarActivityResponse;
import team.projectpulse.ram.exception.InvalidActivityRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.ActivityCategory;
import team.projectpulse.ram.model.ActivityStatus;
import team.projectpulse.ram.model.Instructor;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.model.Team;
import team.projectpulse.ram.model.WarActivity;
import team.projectpulse.ram.repository.InstructorRepository;
import team.projectpulse.ram.repository.StudentRepository;
import team.projectpulse.ram.repository.TeamRepository;
import team.projectpulse.ram.repository.WarActivityRepository;

@Service
public class WarActivityService {

    private final WarActivityRepository warActivityRepository;
    private final StudentRepository studentRepository;
    private final TeamRepository teamRepository;
    private final InstructorRepository instructorRepository;

    public WarActivityService(
            WarActivityRepository warActivityRepository,
            StudentRepository studentRepository,
            TeamRepository teamRepository,
            InstructorRepository instructorRepository
    ) {
        this.warActivityRepository = warActivityRepository;
        this.studentRepository = studentRepository;
        this.teamRepository = teamRepository;
        this.instructorRepository = instructorRepository;
    }

    @Transactional
    public WarActivityResponse createActivity(WarActivityRequest request) {
        validateRequest(request);

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        WarActivity activity = new WarActivity();
        applyActivity(activity, request, student);
        return WarActivityResponse.fromEntity(warActivityRepository.save(activity));
    }

    @Transactional
    public WarActivityResponse updateActivity(Long id, WarActivityRequest request) {
        validateRequest(request);

        WarActivity activity = warActivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WAR activity not found with id: " + id));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        applyActivity(activity, request, student);
        return WarActivityResponse.fromEntity(warActivityRepository.save(activity));
    }

    @Transactional
    public void deleteActivity(Long id) {
        if (!warActivityRepository.existsById(id)) {
            throw new ResourceNotFoundException("WAR activity not found with id: " + id);
        }

        warActivityRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<WarActivityResponse> getStudentActivities(Long studentId, Integer weekNumber) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        List<WarActivity> activities = weekNumber == null
                ? warActivityRepository.findByStudentIdOrderByWeekNumberDescIdDesc(studentId)
                : warActivityRepository.findByStudentIdAndWeekNumberOrderByIdDesc(studentId, weekNumber);

        return activities.stream().map(WarActivityResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public StudentWarReportResponse getStudentWarReport(Long studentId, Integer weekNumber) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        List<WarActivity> activities = weekNumber == null
                ? warActivityRepository.findByStudentIdOrderByWeekNumberDescIdDesc(studentId)
                : warActivityRepository.findByStudentIdAndWeekNumberOrderByIdDesc(studentId, weekNumber);

        return new StudentWarReportResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                weekNumber,
                activities.size(),
                sumPlannedHours(activities),
                sumActualHours(activities),
                activities.stream().map(WarActivityResponse::fromEntity).toList()
        );
    }

    @Transactional(readOnly = true)
    public TeamWarReportResponse getTeamWarReport(Long teamId, String viewerType, Long viewerId, Integer weekNumber) {
        Team team = teamRepository.findByIdWithDetails(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        validateTeamReportAccess(team, viewerType, viewerId);

        List<WarActivity> activities = weekNumber == null
                ? warActivityRepository.findByTeamIdOrderByWeekNumberDescIdDesc(teamId)
                : warActivityRepository.findTeamActivitiesForWeek(teamId, weekNumber);

        Map<Long, TeamWarStudentSummary> summaryByStudent = new LinkedHashMap<>();
        for (WarActivity activity : activities) {
            Student student = activity.getStudent();
            TeamWarStudentSummary current = summaryByStudent.get(student.getId());
            double plannedHours = activity.getPlannedHours() == null ? 0.0 : activity.getPlannedHours();
            double actualHours = activity.getActualHours() == null ? 0.0 : activity.getActualHours();

            if (current == null) {
                summaryByStudent.put(student.getId(), new TeamWarStudentSummary(
                        student.getId(),
                        student.getFirstName() + " " + student.getLastName(),
                        1,
                        plannedHours,
                        actualHours
                ));
            } else {
                summaryByStudent.put(student.getId(), new TeamWarStudentSummary(
                        current.getStudentId(),
                        current.getStudentName(),
                        current.getActivityCount() + 1,
                        current.getPlannedHours() + plannedHours,
                        current.getActualHours() + actualHours
                ));
            }
        }

        return new TeamWarReportResponse(
                team.getId(),
                team.getName(),
                weekNumber,
                viewerType == null ? "UNKNOWN" : viewerType.toUpperCase(Locale.ROOT),
                activities.size(),
                sumPlannedHours(activities),
                sumActualHours(activities),
                List.copyOf(summaryByStudent.values()),
                activities.stream().map(WarActivityResponse::fromEntity).toList()
        );
    }

    @Transactional(readOnly = true)
    public StudentWarReportResponse getInstructorStudentWarReport(Long instructorId, Long studentId, Integer weekNumber) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (instructor.getSection() == null || student.getSection() == null
                || !instructor.getSection().getId().equals(student.getSection().getId())) {
            throw new InvalidActivityRequestException("Instructor does not have access to this student.");
        }

        return getStudentWarReport(studentId, weekNumber);
    }

    private void applyActivity(WarActivity activity, WarActivityRequest request, Student student) {
        activity.setStudent(student);
        activity.setTeam(student.getTeam());
        activity.setTitle(request.getTitle().trim());
        activity.setDescription(trimToNull(request.getDescription()));
        activity.setComments(trimToNull(request.getComments()));
        activity.setCategory(ActivityCategory.valueOf(request.getCategory().trim().toUpperCase(Locale.ROOT)));
        activity.setStatus(ActivityStatus.valueOf(request.getStatus().trim().toUpperCase(Locale.ROOT)));
        activity.setPlannedHours(request.getPlannedHours());
        activity.setActualHours(request.getActualHours());
        activity.setWeekNumber(request.getWeekNumber());
    }

    private void validateRequest(WarActivityRequest request) {
        if (request == null) {
            throw new InvalidActivityRequestException("WAR activity request is required.");
        }

        if (request.getStudentId() == null) {
            throw new InvalidActivityRequestException("studentId is required.");
        }

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new InvalidActivityRequestException("Activity title is required.");
        }

        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new InvalidActivityRequestException("Activity category is required.");
        }

        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new InvalidActivityRequestException("Activity status is required.");
        }

        if (request.getWeekNumber() == null || request.getWeekNumber() <= 0) {
            throw new InvalidActivityRequestException("weekNumber must be greater than 0.");
        }

        try {
            ActivityCategory.valueOf(request.getCategory().trim().toUpperCase(Locale.ROOT));
            ActivityStatus.valueOf(request.getStatus().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new InvalidActivityRequestException("Unknown activity category or status.");
        }
    }

    private void validateTeamReportAccess(Team team, String viewerType, Long viewerId) {
        if (viewerType == null || viewerType.isBlank()) {
            throw new InvalidActivityRequestException("viewerType is required.");
        }

        if (viewerId == null) {
            throw new InvalidActivityRequestException("viewerId is required.");
        }

        String normalizedViewerType = viewerType.trim().toUpperCase(Locale.ROOT);
        if ("STUDENT".equals(normalizedViewerType)) {
            Student student = studentRepository.findById(viewerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + viewerId));

            if (student.getTeam() == null || !student.getTeam().getId().equals(team.getId())) {
                throw new InvalidActivityRequestException("Student is not assigned to the requested team.");
            }

            return;
        }

        if ("INSTRUCTOR".equals(normalizedViewerType)) {
            Instructor instructor = instructorRepository.findById(viewerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + viewerId));

            if (instructor.getSection() == null
                    || team.getSection() == null
                    || !instructor.getSection().getId().equals(team.getSection().getId())) {
                throw new InvalidActivityRequestException("Instructor does not have access to the requested team.");
            }

            return;
        }

        throw new InvalidActivityRequestException("viewerType must be STUDENT or INSTRUCTOR.");
    }

    private double sumPlannedHours(List<WarActivity> activities) {
        return activities.stream()
                .map(WarActivity::getPlannedHours)
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private double sumActualHours(List<WarActivity> activities) {
        return activities.stream()
                .map(WarActivity::getActualHours)
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}
