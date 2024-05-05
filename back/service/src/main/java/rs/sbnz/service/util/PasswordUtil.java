package rs.sbnz.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    @Autowired private PasswordEncoder passwordEncoder;

    public String encode(String passwordPlainText) {
        return passwordEncoder.encode(passwordPlainText);
    }
    
    public boolean doPasswordsMatch(String passwordPlainText, String passwordEncoded) {
        return passwordEncoder.matches(passwordPlainText, passwordEncoded);
    }
}
