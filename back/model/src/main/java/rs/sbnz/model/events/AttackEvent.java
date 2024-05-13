package rs.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

import rs.sbnz.model.AttackType;

@Role(Role.Type.EVENT)
@Expires("24h")
public class AttackEvent {
    private AttackType type;

    public AttackEvent() {
    }

    public AttackEvent(AttackType type) {
        this.type = type;
    }

    public AttackType getType() {
        return this.type;
    }

    public void setType(AttackType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AttackEvent [type=" + type + "]";
    }

}
