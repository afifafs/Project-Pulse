package team.projectpulse.ram.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InviteStudentsRequest {

    private List<String> emails = new ArrayList<>();

    public InviteStudentsRequest() {
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(Object emails) {
        if (emails instanceof String emailText) {
            this.emails = Arrays.stream(emailText.split(";"))
                    .map(String::trim)
                    .filter(email -> !email.isEmpty())
                    .toList();
            return;
        }

        if (emails instanceof List<?> emailList) {
            this.emails = emailList.stream()
                    .map(String::valueOf)
                    .toList();
            return;
        }

        this.emails = new ArrayList<>();
    }
}
