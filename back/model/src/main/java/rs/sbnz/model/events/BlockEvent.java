package rs.sbnz.model.events;

import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Role;

import rs.sbnz.model.BlockReason;

@Role(Role.Type.EVENT)
@Duration("duration")
public class BlockEvent {
    /** Blocked IP address. */
    private String ip;

    /** Duration of block (in ms). */
    private Long duration;
    
    /** Reason for block. */
    private BlockReason reason;

    public BlockEvent() {
    }

    public BlockEvent(String ip, Long durationInMs, BlockReason reason) {
        this.ip = ip;
        this.reason = reason;
        this.duration = durationInMs;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public BlockReason getReason() {
        return this.reason;
    }

    public void setReason(BlockReason reason) {
        this.reason = reason;
    }
}