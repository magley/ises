package rs.sbnz.service.util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import rs.sbnz.service.user.UserService;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {
    @Autowired private JWTUtil jwtUtil;
    @Autowired private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        doFilter(request, response);
        filterChain.doFilter(request, response);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getRequestURL().toString().contains("/api/")) {
            return;
        }

        try {
            String jwt = getJWTFromRequest(request);
            UsernamePasswordAuthenticationToken authToken = getAuthFromJWT(jwt);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            return;
        }
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        return jwtUtil.getJwtFromHeader(request.getHeader("Authorization"));
    }

    private UsernamePasswordAuthenticationToken getAuthFromJWT(String jwt) {
        String email = jwtUtil.getUsernameFromJWT(jwt);
        UserDetails userDetails = userService.findByEmail(email).get();
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
    }
}
