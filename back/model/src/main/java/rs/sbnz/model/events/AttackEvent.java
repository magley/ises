package rs.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

import rs.sbnz.model.AttackSeverity;
import rs.sbnz.model.AttackType;

@Role(Role.Type.EVENT)
@Expires("24h")
public class AttackEvent {
    private AttackType type;

    /** A flag used to check whether this event should be considered for further
     * rules. Basically, the idea is to not use the same attack event twice when
     * detecting large-scale attacks on the server. Maybe just deleting the
     * event would suffice? */
    private boolean madeAwareOf; 

    private AttackSeverity severity;

    public AttackEvent() {
        this.madeAwareOf = false;
    }

    public AttackEvent(AttackType type, AttackSeverity severity) {
        this.type = type;
        this.madeAwareOf = false;
        this.severity = severity;
    }


    public AttackType getType() {
        return this.type;
    }

    public void setType(AttackType type) {
        this.type = type;
    }

    public boolean isMadeAwareOf() {
        return this.madeAwareOf;
    }

    public void setMadeAwareOf(boolean madeAwareOf) {
        this.madeAwareOf = madeAwareOf;
    }

    public AttackSeverity getSeverity() {
        return this.severity;
    }

    public void setSeverity(AttackSeverity severity) {
        this.severity = severity;
    }

    @Override
    public String toString() {
        return "{" +
            " type='" + getType() + "'" +
            ", madeAwareOf='" + isMadeAwareOf() + "'" +
            ", severity='" + getSeverity() + "'" +
            "}";
    }
}
