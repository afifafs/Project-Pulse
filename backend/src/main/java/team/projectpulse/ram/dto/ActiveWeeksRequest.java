package team.projectpulse.ram.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActiveWeeksRequest {

    private List<LocalDate> inactiveWeeks = new ArrayList<>();

    public List<LocalDate> getInactiveWeeks() {
        return inactiveWeeks;
    }

    public void setInactiveWeeks(List<LocalDate> inactiveWeeks) {
        this.inactiveWeeks = inactiveWeeks;
    }
}
