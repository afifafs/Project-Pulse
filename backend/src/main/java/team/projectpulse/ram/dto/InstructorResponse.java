package team.projectpulse.ram.dto;

import team.projectpulse.ram.model.Instructor;

public class InstructorResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;

    public InstructorResponse(Long id, String firstName, String lastName, String email, boolean active) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
    }

    public static InstructorResponse fromEntity(Instructor instructor) {
        return new InstructorResponse(
                instructor.getId(),
                instructor.getFirstName(),
                instructor.getLastName(),
                instructor.getEmail(),
                instructor.isActive()
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

    public boolean isActive() {
        return active;
    }
}
