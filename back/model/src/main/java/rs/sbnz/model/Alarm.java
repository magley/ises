package rs.sbnz.model;

import java.util.UUID;

import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

@Role(Type.EVENT)
public class Alarm {
    /**
     * The UUID is used to "tag" the Alarm fact so that `AlarmRemove` can refer
     * to a specific `Alarm`. An ID would've also worked, but then we'd need to
     * keep track of IDs (i.e. we need state).
     */
    private String uuid;

    private AlarmSeverity severity;
    private String description;
    private boolean handled;
    private AlarmType type;

    public Alarm() {
        this.uuid = UUID.randomUUID().toString();
        this.handled = false;
    }

    public Alarm(AlarmSeverity severity, AlarmType type, String description) {
        this.uuid = UUID.randomUUID().toString();
        this.severity = severity;
        this.type = type;
        this.description = description;
        this.handled = false;
    }
    public AlarmSeverity getSeverity() {
        return this.severity;
    }

    public void setSeverity(AlarmSeverity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHandled() {
        return this.handled;
    }

    public boolean getHandled() {
        return this.handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public AlarmType getType() {
        return this.type;
    }

    public void setType(AlarmType type) {
        this.type = type;
    }    
}
