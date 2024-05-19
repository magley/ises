package rs.sbnz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rs.sbnz.model.api.Packet;
import rs.sbnz.model.events.BlockEvent;
import rs.sbnz.service.request.RequestService;
import rs.sbnz.service.util.RBACUtil;

@RestController
@RequestMapping("api/test")
public class TestController {
    @Autowired private RequestService requestService;
    @Autowired private RBACUtil rbacUtil;

    
    @GetMapping("/gimmie/ip-block")
    public ResponseEntity<?> ipBlock(Packet packet, @RequestParam Long durationMs) {
        requestService.onRequest(packet);
        BlockEvent event = requestService.TEMP_force_block_ip(packet.getSrcIp(), durationMs);

        return new ResponseEntity<String>(event.getIp() + " has been blocked for " + durationMs + "ms!", HttpStatus.OK);
    }

    // NOTE: /api/test allows unauthenticated users to access
    // (SecurityConfig.java), these endpoints were written with security in
    // mind. Not that it matters because they were for testing authorization
    // which is [at the time of writing this] under maintenance because of
    // backward chaining logic. 

    @GetMapping("/any")
    public ResponseEntity<?> test1(Packet packet) {
        requestService.onRequest(packet);

        return new ResponseEntity<String>("Hello any user!", HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> test2(Packet packet) {
        requestService.onRequest(packet);
        rbacUtil.preAuthorize2("view_reports"); // i.e. needs admin.

        return new ResponseEntity<String>("Hello admin!", HttpStatus.OK);
    }

    @GetMapping("/client")
    public ResponseEntity<?> test3(Packet packet) {
        requestService.onRequest(packet);
        rbacUtil.preAuthorize2("buy_articles"); // i.e. needs client.

        return new ResponseEntity<String>("Hello client!", HttpStatus.OK);
    }
}
