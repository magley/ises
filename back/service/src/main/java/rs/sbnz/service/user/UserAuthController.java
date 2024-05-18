package rs.sbnz.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.sbnz.model.Request;
import rs.sbnz.model.User;
import rs.sbnz.model.api.Packet;
import rs.sbnz.model.events.LoginEvent;
import rs.sbnz.service.exceptions.UnauthorizedException;
import rs.sbnz.service.request.RequestService;
import rs.sbnz.service.user.dto.LoginDTO;
import rs.sbnz.service.user.dto.RegisterDTO;
import rs.sbnz.service.util.JWTUtil;
import rs.sbnz.service.util.RBACUtil;

@RestController
@RequestMapping("api/auth")
public class UserAuthController {
    @Autowired private RequestService requestService;
    @Autowired private UserService userService;
    @Autowired private JWTUtil jwtUtil;
    @Autowired private AuthenticationManager authManager;
    @Autowired private RBACUtil rbacUtil;

    @GetMapping("/permission")
    public ResponseEntity<?> checkForPermission(Packet packet, String permissionCode) {
        requestService.onRequest(packet);

        try {
            rbacUtil.preAuthorize2(permissionCode);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } catch (UnauthorizedException ex) {
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(Packet packet, @RequestBody RegisterDTO dto) {
        Request request = requestService.onRequest(packet);
        requestService.onTextSubmission(dto.getEmail(), request);
        requestService.onTextSubmission(dto.getPassword(), request);
        requestService.onTextSubmission(dto.getPasswordConfirm(), request);
        requestService.onTextSubmission(dto.getName(), request);
        requestService.onTextSubmission(dto.getLastName(), request);

        userService.add(dto.getEmail(), dto.getPassword(), dto.getName(), dto.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(Packet packet, @RequestBody LoginDTO dto) {
        Request request = requestService.onRequest(packet);
        requestService.onTextSubmission(dto.getEmail(), request);
        requestService.onTextSubmission(dto.getPassword(), request);

        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
            Authentication auth = authManager.authenticate(authToken);
            User user = (User)auth.getPrincipal();

            String roleName = "";
            if (user.getRbacRole() != null) {
                roleName = user.getRbacRole().getName();
            }

            boolean isWeakPassword = requestService.onLoginAttempt(dto.getEmail(), dto.getPassword());
            
            String jwt = jwtUtil.generateJWT(user.getUsername(), user.getId(), roleName, isWeakPassword);
            return ResponseEntity.ok(jwt);
        } catch (AuthenticationException ex) {
            requestService.onFailedLogin(packet.getSrcIp(), dto.getEmail());
            throw ex;
        }
    }
}