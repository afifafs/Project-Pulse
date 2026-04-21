package team.projectpulse.ram.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.AssignStudentsRequest;
import team.projectpulse.ram.dto.TeamRequest;
import team.projectpulse.ram.dto.TeamResponse;
import team.projectpulse.ram.exception.DuplicateResourceException;
import team.projectpulse.ram.exception.InvalidTeamRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.model.Team;
import team.projectpulse.ram.repository.SectionRepository;
import team.projectpulse.ram.repository.StudentRepository;
import team.projectpulse.ram.repository.TeamRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;

    public TeamService(
            TeamRepository teamRepository,
            SectionRepository sectionRepository,
            StudentRepository studentRepository
    ) {
        this.teamRepository = teamRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
    }

    public Team createTeam(Team team) {
        return null;
    }

    @Transactional
    public TeamResponse createTeam(TeamRequest request) {
        validateTeamRequest(request);

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));

        Team team = new Team();
        team.setName(request.getName().trim());
        team.setDescription(request.getDescription());
        team.setWebsite(request.getWebsite());
        team.setSection(section);

        Team createdTeam = teamRepository.save(team);
        return TeamResponse.fromEntity(createdTeam);
    }

    public Optional<Team> getTeamById(Long id) {
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> findTeams(String sectionName, String teamName) {
        return teamRepository.findTeams(normalizeFilter(sectionName), normalizeFilter(teamName)).stream()
                .map(TeamResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeamDetails(Long id) {
        Team team = teamRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));

        return TeamResponse.fromEntity(team);
    }

    public List<Team> getAllTeams() {
        return List.of();
    }

    public Team updateTeam(Long id, Team team) {
        return null;
    }

    @Transactional
    public TeamResponse assignStudentsToTeam(Long teamId, AssignStudentsRequest request) {
        Team team = teamRepository.findByIdWithDetails(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        Set<Long> studentIds = validateAndNormalizeStudentIds(request);
        List<Student> students = studentRepository.findByIdIn(studentIds);

        if (students.size() != studentIds.size()) {
            Set<Long> foundStudentIds = students.stream()
                    .map(Student::getId)
                    .collect(java.util.stream.Collectors.toSet());
            Long missingStudentId = studentIds.stream()
                    .filter(studentId -> !foundStudentIds.contains(studentId))
                    .findFirst()
                    .orElse(null);

            throw new ResourceNotFoundException("Student not found with id: " + missingStudentId);
        }

        for (Student student : students) {
            student.setTeam(team);
            if (student.getSection() == null) {
                student.setSection(team.getSection());
            }
        }

        studentRepository.saveAll(students);
        team.setStudents(students);

        return TeamResponse.fromEntity(team);
    }

    @Transactional
    public TeamResponse updateTeam(Long id, TeamRequest request) {
        validateTeamUpdateRequest(request);

        Team team = teamRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));

        String updatedName = request.getName().trim();
        if (!team.getName().equalsIgnoreCase(updatedName)
                && teamRepository.existsByNameIgnoreCaseAndIdNot(updatedName, id)) {
            throw new DuplicateResourceException("A team with this name already exists.");
        }

        team.setName(updatedName);
        team.setDescription(request.getDescription());
        team.setWebsite(request.getWebsite());

        Team updatedTeam = teamRepository.save(team);
        return TeamResponse.fromEntity(updatedTeam);
    }

    public void deleteTeam(Long id) {
    }

    private void validateTeamRequest(TeamRequest request) {
        if (request == null) {
            throw new InvalidTeamRequestException("Team request is required.");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidTeamRequestException("Team name is required.");
        }

        if (teamRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("A team with this name already exists.");
        }

        if (request.getSectionId() == null) {
            throw new InvalidTeamRequestException("sectionId is required.");
        }
    }

    private void validateTeamUpdateRequest(TeamRequest request) {
        if (request == null) {
            throw new InvalidTeamRequestException("Team request is required.");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidTeamRequestException("Team name is required.");
        }
    }

    private Set<Long> validateAndNormalizeStudentIds(AssignStudentsRequest request) {
        if (request == null || request.getStudentIds() == null || request.getStudentIds().isEmpty()) {
            throw new InvalidTeamRequestException("At least one studentId is required.");
        }

        Set<Long> studentIds = new HashSet<>();
        for (Long studentId : request.getStudentIds()) {
            if (studentId == null) {
                throw new InvalidTeamRequestException("studentIds cannot contain null values.");
            }

            studentIds.add(studentId);
        }

        return studentIds;
    }

    private String normalizeFilter(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}
