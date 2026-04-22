package team.projectpulse.ram.dto;

import java.time.LocalDate;
import java.util.List;

public class SectionDetailResponse {

    private Long id;

    private String name;

    private String courseCode;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<TeamDetailResponse> teams;

    private List<StudentResponse> studentsNotAssignedToTeams;

    private List<String> instructors;

    private RubricDetailResponse rubric;

    public SectionDetailResponse(
            Long id,
            String name,
            String courseCode,
            LocalDate startDate,
            LocalDate endDate,
            List<TeamDetailResponse> teams,
            List<StudentResponse> studentsNotAssignedToTeams,
            List<String> instructors,
            RubricDetailResponse rubric
    ) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.teams = teams;
        this.studentsNotAssignedToTeams = studentsNotAssignedToTeams;
        this.instructors = instructors;
        this.rubric = rubric;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<TeamDetailResponse> getTeams() {
        return teams;
    }

    public List<StudentResponse> getStudentsNotAssignedToTeams() {
        return studentsNotAssignedToTeams;
    }

    public List<String> getInstructors() {
        return instructors;
    }

    public RubricDetailResponse getRubric() {
        return rubric;
    }
}
