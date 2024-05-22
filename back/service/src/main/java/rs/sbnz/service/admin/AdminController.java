package rs.sbnz.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.sbnz.model.api.Packet;
import rs.sbnz.model.events.BlockEvent;
import rs.sbnz.service.request.RequestService;
import rs.sbnz.service.user.UserService;
import rs.sbnz.service.util.AuthenticationFacade;
import rs.sbnz.service.util.RBACUtil;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    @Autowired private RequestService requestService;
    @Autowired private RBACUtil rbacUtil;
    @Autowired private AdminService adminService;
    @Autowired private UserService userService;
    @Autowired private AuthenticationFacade authenticationFacade;

    @GetMapping("/blocks")
    public ResponseEntity<?> getAllBlocks(Packet packet) {
        requestService.onRequest(packet);
        rbacUtil.preAuthorize2("unban");
        List<BlockEvent> blockEvents = adminService.getBlockEvents();
        return ResponseEntity.status(HttpStatus.OK).body(blockEvents);
    }

    @PostMapping("/unblock/{ip}")
    public ResponseEntity<?> unblockIP(Packet packet, @PathVariable String ip) {
        requestService.onRequest(packet);
        rbacUtil.preAuthorize2("unban");
        adminService.unblockIP(ip);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
