package rs.sbnz.model;

import javax.persistence.OneToMany;

public class RbacRequest {
    private User user;
    private Permission permission;
    private boolean rejected;

    public RbacRequest() {
        this.rejected = false;
    }

    public RbacRequest(User user, Permission permission) {
        this.user = user;
        this.permission = permission;
        this.rejected = false;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public boolean getRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }
}
