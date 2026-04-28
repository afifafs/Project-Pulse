package team.projectpulse.ram.dto;

import java.util.ArrayList;
import java.util.List;

public class AssignInstructorsRequest {

    private List<Long> instructorIds = new ArrayList<>();

    public List<Long> getInstructorIds() {
        return instructorIds;
    }

    public void setInstructorIds(List<Long> instructorIds) {
        this.instructorIds = instructorIds;
    }
}
