package team.projectpulse.ram.dto;

import team.projectpulse.ram.model.Instructor;

public class InstructorResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private Boolean active;
    private Long sectionId;
    private String sectionName;

    public InstructorResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String username,
            Boolean active,
            Long sectionId,
            String sectionName
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.active = active;
        this.sectionId = sectionId;
        this.sectionName = sectionName;
    }

    public static InstructorResponse fromEntity(Instructor instructor) {
        return new InstructorResponse(
                instructor.getId(),
                instructor.getFirstName(),
                instructor.getLastName(),
                instructor.getEmail(),
                instructor.getUsername(),
                instructor.getActive(),
                instructor.getSection() == null ? null : instructor.getSection().getId(),
                instructor.getSection() == null ? null : instructor.getSection().getName()
        );
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getActive() {
        return active;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }
}
