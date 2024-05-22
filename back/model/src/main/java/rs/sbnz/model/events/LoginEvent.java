package rs.sbnz.model.events;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Expires("6h")
public class LoginEvent {
    /** Email of the account that was attempt to be logged in. */
    private String email;

    /** Password that was submitted while logging in. */
    private String password;

    /** Flag that gets set by the rule engine in case 'password' is a WeakPassword.
     * When the user successfully logs in with a weak password, a flag is set in
     * the JWT so that he gets nagged by the web app to change his password.
     */
    private boolean weakPassword;

    public LoginEvent() {
    }

    public LoginEvent(String email, String password) {
        this.email = email;
        this.password = password;
        this.weakPassword = false;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isWeakPassword() {
        return this.weakPassword;
    }

    public boolean getWeakPassword() {
        return this.weakPassword;
    }

    public void setWeakPassword(boolean weakPassword) {
        this.weakPassword = weakPassword;
    }

}
