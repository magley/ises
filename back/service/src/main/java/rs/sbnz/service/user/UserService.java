package rs.sbnz.service.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import rs.sbnz.model.User;
import rs.sbnz.model.UserRole;
import rs.sbnz.service.util.PasswordUtil;

@Component
public class UserService implements UserDetailsService {
    @Autowired private IUserRepo userRepo;
    @Autowired private PasswordUtil passwordUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Make EntityNotFound exception.
        User user = userRepo
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
        return user;
    }

    /**
     * Add a new User into the database.
     * 
     * @param email Email.
     * @param passwordPlaintext Password provided in plain text.
     * @param name Name.
     * @param lastName Last name.
     * @return The newly-created User.
     */
    public User add(String email, String passwordPlaintext, String name, String lastName) {
        User user = new User(null, email, passwordPlaintext, UserRole.CLIENT, name, lastName);
        user.setPassword(passwordUtil.encode(passwordPlaintext));
        user = userRepo.save(user);
        return user;
    }

    /**
     * Try to find a User matching the provided email. Does not throw if none
     * found.
     * @param email Email of the user.
     * @return An Optional User.
     */
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
