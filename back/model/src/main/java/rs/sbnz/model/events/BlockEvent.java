package rs.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Expires("24h")
public class BlockEvent {
    /** Blocked IP address. */
    private String ip;

    public BlockEvent() {
    }

    public BlockEvent(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}