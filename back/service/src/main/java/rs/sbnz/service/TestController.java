package rs.sbnz.service;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rs.sbnz.model.Request;

@RestController
@RequestMapping("api/test")
public class TestController {
    @Autowired private KieSession ksession;

    @GetMapping
    public ResponseEntity<?> basicGetTest(@RequestParam String srcIp, @RequestParam String destIp) {
        ksession.insert(new Request(srcIp, destIp));
        ksession.fireAllRules();

        return new ResponseEntity<String>("Hello from the backend!", HttpStatus.OK);
    }
}
