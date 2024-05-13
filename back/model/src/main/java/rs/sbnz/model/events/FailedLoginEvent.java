package rs.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Expires("6h")
public class FailedLoginEvent {
    private Long id;

    /** Source IP address of the failed login attempt. */
    private String ip;

    /** Email of the account that was attempt to be logged in. */
    private String email;

    public FailedLoginEvent() {
    }

    public FailedLoginEvent(Long id, String ip, String email) {
        this.id = id;
        this.ip = ip;
        this.email = email;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "FailedLoginEvent [id=" + id + ", ip=" + ip + ", email=" + email + "]";
    }

    
}
