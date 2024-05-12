package rs.sbnz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.sbnz.service.request.RequestService;

@RestController
@RequestMapping("api/test")
public class TestController {
    @Autowired private RequestService requestService;

    @GetMapping("/any")
    public ResponseEntity<?> test1(@RequestParam String srcIp, @RequestParam String destIp, @RequestParam String srcPort) {
        requestService.onRequest(srcIp, destIp, srcPort);

        return new ResponseEntity<String>("Hello any authenticated user!", HttpStatus.OK);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> test2(@RequestParam String srcIp, @RequestParam String destIp, @RequestParam String srcPort) {
        requestService.onRequest(srcIp, destIp, srcPort);

        return new ResponseEntity<String>("Hello admin!", HttpStatus.OK);
    }

    @GetMapping("/client")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<?> test3(@RequestParam String srcIp, @RequestParam String destIp, @RequestParam String srcPort) {
        requestService.onRequest(srcIp, destIp, srcPort);

        return new ResponseEntity<String>("Hello client!", HttpStatus.OK);
    }
}
