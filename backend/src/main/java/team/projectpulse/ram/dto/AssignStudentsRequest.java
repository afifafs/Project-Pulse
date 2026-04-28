package team.projectpulse.ram.dto;

import java.util.ArrayList;
import java.util.List;

public class AssignStudentsRequest {

    private List<Long> studentIds = new ArrayList<>();

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }
}
