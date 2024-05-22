package rs.sbnz.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.sbnz.model.RbacRequest;
import rs.sbnz.model.Request;
import rs.sbnz.model.User;
import rs.sbnz.model.api.Packet;
import rs.sbnz.service.exceptions.NewPasswordIsWeakException;
import rs.sbnz.service.exceptions.WrongPasswordException;
import rs.sbnz.service.request.RequestService;
import rs.sbnz.service.user.dto.PasswordChangeDTO;
import rs.sbnz.service.user.dto.SetUserRoleDTO;
import rs.sbnz.service.user.dto.UserDTO;
import rs.sbnz.service.util.AuthenticationFacade;
import rs.sbnz.service.util.RBACUtil;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired private RequestService requestService;
    @Autowired private UserService userService;
    @Autowired private AuthenticationFacade authenticationFacade;
    @Autowired private RBACUtil rbacUtil;

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(Packet packet, @PathVariable Long id) {
        requestService.onRequest(packet);
        User user = userService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new UserDTO(user));
    }

    @GetMapping()
    public ResponseEntity<?> findAll(Packet packet) {
        requestService.onRequest(packet);
        List<UserDTO> users = userService.findAll().stream().map(u -> new UserDTO(u)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PostMapping("/passchange")
    public ResponseEntity<?> changePassword(Packet packet, @RequestBody PasswordChangeDTO dto) {
        Request request =  requestService.onRequest(packet);
        User user = authenticationFacade.getUser();
        requestService.onTextSubmission(dto.getCurrentPassword(), request);
        requestService.onTextSubmission(dto.getNewPassword(), request);

        try {
            userService.changePassword(user, dto);
        } catch (WrongPasswordException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong password.");
        }  catch (NewPasswordIsWeakException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password is weak. Try a different password.");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PostMapping("/role")
    public ResponseEntity<?> setRole(Packet packet, @RequestBody SetUserRoleDTO dto) {
        requestService.onRequest(packet);
        rbacUtil.preAuthorize2("change_role");

        User user = authenticationFacade.getUser();
        if (user.getId() == dto.getUserId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot change your own role.");
        }

        userService.changeRole(dto.getUserId(), dto.getRoleId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
