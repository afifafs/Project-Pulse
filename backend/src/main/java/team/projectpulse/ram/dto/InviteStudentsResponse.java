package team.projectpulse.ram.dto;

public class InviteStudentsResponse {

    private int invitationsSent;

    public InviteStudentsResponse(int invitationsSent) {
        this.invitationsSent = invitationsSent;
    }

    public int getInvitationsSent() {
        return invitationsSent;
    }
}
