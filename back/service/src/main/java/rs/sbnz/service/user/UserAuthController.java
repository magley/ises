package rs.sbnz.service.user;

import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.sbnz.model.User;
import rs.sbnz.model.api.Packet;
import rs.sbnz.service.request.RequestService;
import rs.sbnz.service.user.dto.LoginDTO;
import rs.sbnz.service.user.dto.RegisterDTO;
import rs.sbnz.service.util.JWTUtil;

@RestController
@RequestMapping("api/auth")
public class UserAuthController {
    @Autowired private RequestService requestService;
    @Autowired private UserService userService;
    @Autowired private JWTUtil jwtUtil;
    @Autowired private AuthenticationManager authManager;

    @PermitAll
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(Packet packet, @RequestBody RegisterDTO dto) {
        requestService.onRequest(packet);

        userService.add(dto.getEmail(), dto.getPassword(), dto.getName(), dto.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PermitAll
    @PostMapping("/login")
    public ResponseEntity<?> login(Packet packet, @RequestBody LoginDTO dto) {
        requestService.onRequest(packet);
        
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
            Authentication auth = authManager.authenticate(authToken);
            User user = (User)auth.getPrincipal();
            String jwt = jwtUtil.generateJWT(user.getUsername(), user.getId(), user.getRole().toString());
            return ResponseEntity.ok(jwt);
        } catch (AuthenticationException ex) {
            requestService.onFailedLogin(packet.getSrcIp(), dto.getEmail());
            throw ex;
        }
    }
}