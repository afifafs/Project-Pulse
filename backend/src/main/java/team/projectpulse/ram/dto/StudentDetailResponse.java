package team.projectpulse.ram.dto;

public class StudentDetailResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;
    private String sectionName;
    private String teamName;

    public StudentDetailResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            boolean active,
            String sectionName,
            String teamName
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.sectionName = sectionName;
        this.teamName = teamName;
    }

    public static StudentDetailResponse fromEntity(team.projectpulse.ram.model.Student student) {
        return new StudentDetailResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.isActive(),
                student.getSection() == null ? null : student.getSection().getName(),
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

    public boolean isActive() {
        return active;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getTeamName() {
        return teamName;
    }
}
