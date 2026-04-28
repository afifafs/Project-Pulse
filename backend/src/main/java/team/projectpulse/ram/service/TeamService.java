package team.projectpulse.ram.service;

import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.AssignInstructorsRequest;
import team.projectpulse.ram.dto.AssignStudentsRequest;
import team.projectpulse.ram.dto.InstructorResponse;
import team.projectpulse.ram.dto.StudentResponse;
import team.projectpulse.ram.dto.TeamDetailResponse;
import team.projectpulse.ram.dto.TeamRequest;
import team.projectpulse.ram.exception.DuplicateResourceException;
import team.projectpulse.ram.exception.InvalidStudentRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Instructor;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.model.Team;
import team.projectpulse.ram.repository.InstructorRepository;
import team.projectpulse.ram.repository.SectionRepository;
import team.projectpulse.ram.repository.StudentRepository;
import team.projectpulse.ram.repository.TeamRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;

    public TeamService(
            TeamRepository teamRepository,
            SectionRepository sectionRepository,
            StudentRepository studentRepository,
            InstructorRepository instructorRepository
    ) {
        this.teamRepository = teamRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
    }

    @Transactional
    public TeamDetailResponse createTeam(TeamRequest request) {
        validateTeamRequest(request, null);

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));

        Team team = new Team();
        team.setName(request.getName().trim());
        team.setDescription(normalizeOptional(request.getDescription()));
        team.setWebsite(normalizeOptional(request.getWebsite()));
        team.setSection(section);

        return map(teamRepository.save(team));
    }

    @Transactional(readOnly = true)
    public TeamDetailResponse getTeamById(Long id) {
        Team team = teamRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        return map(team);
    }

    @Transactional(readOnly = true)
    public List<TeamDetailResponse> getAllTeams(String sectionName, String teamName) {
        return teamRepository.search(normalizeFilter(sectionName), normalizeFilter(teamName)).stream()
                .map(this::map)
                .toList();
    }

    @Transactional
    public TeamDetailResponse updateTeam(Long id, TeamRequest request) {
        Team team = teamRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        validateTeamRequest(request, id);

        team.setName(request.getName().trim());
        team.setDescription(normalizeOptional(request.getDescription()));
        team.setWebsite(normalizeOptional(request.getWebsite()));
        return map(teamRepository.save(team));
    }

    @Transactional
    public TeamDetailResponse assignStudents(Long teamId, AssignStudentsRequest request) {
        Team team = teamRepository.findByIdWithDetails(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        if (request == null || request.getStudentIds() == null || request.getStudentIds().isEmpty()) {
            throw new InvalidStudentRequestException("At least one studentId is required.");
        }

        List<Student> students = studentRepository.findByIdIn(request.getStudentIds());
        if (students.size() != new LinkedHashSet<>(request.getStudentIds()).size()) {
            throw new ResourceNotFoundException("One or more students were not found.");
        }

        for (Student student : students) {
            if (student.getSection() == null || team.getSection() == null
                    || !student.getSection().getId().equals(team.getSection().getId())) {
                throw new InvalidStudentRequestException("Students can only be assigned to teams in their own section.");
            }
            student.setTeam(team);
        }
        studentRepository.saveAll(students);
        return getTeamById(teamId);
    }

    @Transactional
    public TeamDetailResponse removeStudent(Long teamId, Long studentId) {
        Team team = teamRepository.findByIdWithDetails(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (student.getTeam() == null || !student.getTeam().getId().equals(team.getId())) {
            throw new InvalidStudentRequestException("Student does not belong to this team.");
        }

        student.setTeam(null);
        studentRepository.save(student);
        return getTeamById(teamId);
    }

    @Transactional
    public TeamDetailResponse assignInstructors(Long teamId, AssignInstructorsRequest request) {
        Team team = teamRepository.findByIdWithDetails(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        if (request == null || request.getInstructorIds() == null || request.getInstructorIds().isEmpty()) {
            throw new InvalidStudentRequestException("At least one instructorId is required.");
        }

        List<Instructor> instructors = instructorRepository.findByIdIn(request.getInstructorIds());
        if (instructors.size() != new LinkedHashSet<>(request.getInstructorIds()).size()) {
            throw new ResourceNotFoundException("One or more instructors were not found.");
        }

        Set<Instructor> updated = new LinkedHashSet<>(team.getInstructors());
        updated.addAll(instructors);
        team.setInstructors(updated);

        if (team.getSection() != null) {
            Set<Instructor> sectionInstructors = new LinkedHashSet<>(team.getSection().getInstructors());
            sectionInstructors.addAll(instructors);
            team.getSection().setInstructors(sectionInstructors);
        }

        return map(teamRepository.save(team));
    }

    @Transactional
    public TeamDetailResponse removeInstructor(Long teamId, Long instructorId) {
        Team team = teamRepository.findByIdWithDetails(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        if (!team.getInstructors().remove(instructor)) {
            throw new InvalidStudentRequestException("Instructor is not assigned to this team.");
        }
        return map(teamRepository.save(team));
    }

    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        for (Student student : team.getStudents()) {
            student.setTeam(null);
        }
        teamRepository.delete(team);
    }

    private void validateTeamRequest(TeamRequest request, Long existingId) {
        if (request == null) {
            throw new InvalidStudentRequestException("Team request is required.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidStudentRequestException("Team name is required.");
        }
        boolean exists = existingId == null
                ? teamRepository.existsByNameIgnoreCase(request.getName().trim())
                : teamRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), existingId);
        if (exists) {
            throw new DuplicateResourceException("A team with this name already exists.");
        }
        if (existingId == null && request.getSectionId() == null) {
            throw new InvalidStudentRequestException("sectionId is required.");
        }
    }

    private TeamDetailResponse map(Team team) {
        List<StudentResponse> students = team.getStudents().stream()
                .map(StudentResponse::fromEntity)
                .toList();
        List<InstructorResponse> instructors = team.getInstructors().stream()
                .map(InstructorResponse::fromEntity)
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

    private String normalizeOptional(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String normalizeFilter(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
