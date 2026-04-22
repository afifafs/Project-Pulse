package team.projectpulse.ram.dto;

import team.projectpulse.ram.model.Section;

public class SectionResponse {

    private Long id;

    private String name;

    private String courseCode;

    public SectionResponse() {
    }

    public SectionResponse(Long id, String name, String courseCode) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
    }

    public static SectionResponse fromEntity(Section section) {
        return new SectionResponse(
                section.getId(),
                section.getName(),
                section.getCourseCode()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
