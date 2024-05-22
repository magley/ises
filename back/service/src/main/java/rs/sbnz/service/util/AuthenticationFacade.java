package rs.sbnz.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import rs.sbnz.model.User;
import rs.sbnz.service.exceptions.UnauthorizedException;
import rs.sbnz.service.user.IUserRepo;

@Component
public class AuthenticationFacade {
    @Autowired private IUserRepo userRepo;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public User getUser() {
        String email = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findByEmail(email).orElseThrow(() -> new UnauthorizedException());
    }
}