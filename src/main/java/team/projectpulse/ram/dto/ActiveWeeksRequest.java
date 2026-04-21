package team.projectpulse.ram.dto;

import java.util.ArrayList;
import java.util.List;

public class ActiveWeeksRequest {

    private List<String> inactiveWeeks = new ArrayList<>();

    public ActiveWeeksRequest() {
    }

    public List<String> getInactiveWeeks() {
        return inactiveWeeks;
    }

    public void setInactiveWeeks(List<String> inactiveWeeks) {
        this.inactiveWeeks = inactiveWeeks;
    }
}
