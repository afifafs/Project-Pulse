package team.projectpulse.ram.dto;

import java.util.List;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Team;

public class TeamResponse {

    private Long id;

    private String name;

    private String description;

    private String website;

    private List<StudentResponse> members;

    private List<String> instructors;

    public TeamResponse(
            Long id,
            String name,
            String description,
            String website,
            List<StudentResponse> members,
            List<String> instructors
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.website = website;
        this.members = members;
        this.instructors = instructors;
    }

    public static TeamResponse fromEntity(Team team) {
        Section section = team.getSection();
        List<String> instructors = section == null ? List.of() : List.copyOf(section.getInstructors());

        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getWebsite(),
                team.getStudents().stream()
                        .map(StudentResponse::fromEntity)
                        .toList(),
                instructors
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public List<StudentResponse> getMembers() {
        return members;
    }

    public List<String> getInstructors() {
        return instructors;
    }
}
