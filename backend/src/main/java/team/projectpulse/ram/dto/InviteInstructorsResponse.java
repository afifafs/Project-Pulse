package team.projectpulse.ram.dto;

public class InviteInstructorsResponse {

    private int invitationsSent;

    public InviteInstructorsResponse(int invitationsSent) {
        this.invitationsSent = invitationsSent;
    }

    public int getInvitationsSent() {
        return invitationsSent;
    }
}
