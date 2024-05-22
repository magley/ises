package rs.sbnz.model;

public class WeakPassword {
    private String password;

    public WeakPassword() {
    }

    public WeakPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
