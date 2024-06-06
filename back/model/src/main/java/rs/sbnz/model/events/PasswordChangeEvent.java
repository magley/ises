package rs.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Expires("1m")
public class PasswordChangeEvent {
    private String password;
    private boolean isWeak;

    public PasswordChangeEvent() {
    }


    public PasswordChangeEvent(String password) {
        this.password = password;
        this.isWeak = false;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIsWeak() {
        return this.isWeak;
    }

    public boolean getIsWeak() {
        return this.isWeak;
    }

    public void setIsWeak(boolean isWeak) {
        this.isWeak = isWeak;
    }
}
