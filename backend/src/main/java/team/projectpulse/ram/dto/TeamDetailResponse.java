package team.projectpulse.ram.dto;

import java.util.List;

public class TeamDetailResponse {

    private Long id;

    private String name;

    private List<StudentResponse> students;

    public TeamDetailResponse(Long id, String name, List<StudentResponse> students) {
        this.id = id;
        this.name = name;
        this.students = students;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<StudentResponse> getStudents() {
        return students;
    }
}
