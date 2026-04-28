package team.projectpulse.ram.dto;

import team.projectpulse.ram.model.Student;

public class ProfileResponse {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private String role;

    public ProfileResponse(
            Long id,
            String username,
            String firstName,
            String lastName,
            String email,
            String status,
            String role
    ) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.role = role;
    }

    public static ProfileResponse fromEntity(Student student) {
        return new ProfileResponse(
                student.getId(),
                student.getEmail(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                "Enabled",
                "student"
        );
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
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

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }
}
