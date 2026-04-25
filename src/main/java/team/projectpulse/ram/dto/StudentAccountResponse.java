package team.projectpulse.ram.dto;

import team.projectpulse.ram.model.Student;

public class StudentAccountResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private Boolean active;
    private Long sectionId;
    private String sectionName;
    private Long teamId;
    private String teamName;

    public StudentAccountResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String username,
            Boolean active,
            Long sectionId,
            String sectionName,
            Long teamId,
            String teamName
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.active = active;
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public static StudentAccountResponse fromEntity(Student student) {
        return new StudentAccountResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getUsername(),
                student.getActive(),
                student.getSection() == null ? null : student.getSection().getId(),
                student.getSection() == null ? null : student.getSection().getName(),
                student.getTeam() == null ? null : student.getTeam().getId(),
                student.getTeam() == null ? null : student.getTeam().getName()
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

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }
}
