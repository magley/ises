package rs.sbnz.service.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import rs.sbnz.model.User;
import rs.sbnz.model.UserRole;
import rs.sbnz.service.exceptions.NewPasswordIsWeakException;
import rs.sbnz.service.exceptions.NotFoundException;
import rs.sbnz.service.exceptions.WrongPasswordException;
import rs.sbnz.service.request.RequestService;
import rs.sbnz.service.user.dto.PasswordChangeDTO;
import rs.sbnz.service.util.PasswordUtil;

@Component
public class UserService implements UserDetailsService {
    @Autowired private IUserRepo userRepo;
    @Autowired private PasswordUtil passwordUtil;
    @Autowired private RequestService requestService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Make EntityNotFound exception.
        User user = userRepo
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
        return user;
    }

    public User save(User user) {
        userRepo.save(user);
        return user;
    }

    public User findById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new NotFoundException());
    }

    public void changePassword(User user, PasswordChangeDTO dto) {
        if (!passwordUtil.doPasswordsMatch(dto.getCurrentPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        boolean isWeakPassword = requestService.onChangePassword(dto.getNewPassword());
        if (isWeakPassword) {
            throw new NewPasswordIsWeakException();
        }

        user.setPassword(passwordUtil.encode(dto.getNewPassword()));
        userRepo.save(user);
    }

    public List<User> findAll() {
        return userRepo.findAll();
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
