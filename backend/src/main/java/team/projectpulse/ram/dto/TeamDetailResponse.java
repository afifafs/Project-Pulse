package team.projectpulse.ram.dto;

import java.util.List;

public class TeamDetailResponse {

    private Long id;

    private String name;

    private String sectionName;

    private String description;

    private String website;

    private List<StudentResponse> students;

    private List<InstructorResponse> instructors;

    public TeamDetailResponse(
            Long id,
            String name,
            String sectionName,
            String description,
            String website,
            List<StudentResponse> students,
            List<InstructorResponse> instructors
    ) {
        this.id = id;
        this.name = name;
        this.sectionName = sectionName;
        this.description = description;
        this.website = website;
        this.students = students;
        this.instructors = instructors;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public List<StudentResponse> getStudents() {
        return students;
    }

    public List<InstructorResponse> getInstructors() {
        return instructors;
    }
}
