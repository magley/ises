package rs.sbnz.service.user.dto;

public class IsBlockedDTO {
    private boolean banned;

    public IsBlockedDTO() {
    }

    public IsBlockedDTO(boolean banned) {
        this.banned = banned;
    }

    public boolean isBanned() {
        return this.banned;
    }

    public boolean getBanned() {
        return this.banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
