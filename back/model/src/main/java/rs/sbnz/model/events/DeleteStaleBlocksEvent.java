package rs.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Expires("30s")
public class DeleteStaleBlocksEvent {
    public DeleteStaleBlocksEvent() {
    }    
}
