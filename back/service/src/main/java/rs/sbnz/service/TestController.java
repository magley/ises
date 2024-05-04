package rs.sbnz.service;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.sbnz.service.request.RequestService;

@RestController
@RequestMapping("api/test")
public class TestController {
     @Autowired private RequestService requestService;

    @GetMapping
    public ResponseEntity<?> basicGetTest(@RequestParam String srcIp, @RequestParam String destIp) {
        requestService.onRequest(srcIp, destIp);

        return new ResponseEntity<String>("Hello from the backend!", HttpStatus.OK);
    }
}
