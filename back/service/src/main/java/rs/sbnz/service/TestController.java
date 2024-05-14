package rs.sbnz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rs.sbnz.model.api.Packet;
import rs.sbnz.model.events.BlockEvent;
import rs.sbnz.service.request.RequestService;

@RestController
@RequestMapping("api/test")
public class TestController {
    @Autowired private RequestService requestService;
    
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

        return new ResponseEntity<String>("Hello any authenticated user!", HttpStatus.OK);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> test2(Packet packet) {
        requestService.onRequest(packet);

        return new ResponseEntity<String>("Hello admin!", HttpStatus.OK);
    }

    @GetMapping("/client")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<?> test3(Packet packet) {
        requestService.onRequest(packet);

        return new ResponseEntity<String>("Hello client!", HttpStatus.OK);
    }
}
