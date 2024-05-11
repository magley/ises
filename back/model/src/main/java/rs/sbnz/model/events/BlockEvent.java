package rs.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

import rs.sbnz.model.BlockReason;

@Role(Role.Type.EVENT)
@Expires("24h")
public class BlockEvent {
    /** Blocked IP address. */
    private String ip;
    
    /** Reason for block. */
    private BlockReason reason;

    public BlockEvent() {
    }

    public BlockEvent(String ip, BlockReason reason) {
        this.ip = ip;
        this.reason = reason;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public BlockReason getReason() {
        return this.reason;
    }

    public void setReason(BlockReason reason) {
        this.reason = reason;
    }

}