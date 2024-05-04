package rs.sbnz.service.request;

import java.time.ZonedDateTime;
import java.util.Date;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.sbnz.model.Request;

@Component
public class RequestService {
    @Autowired private KieSession ksession;
    @Autowired private IRequestRepo requestRepo;

    /**
     * Method to call on start of every HTTP Request made to the server.
     * Automatically submits the base request packet to the rule engine.
     * 
     * @param srcIp Source IP address from the packet. 
     * @param destIp Destination IP address from the packet.
     * @return The Request entity.
     */
    public Request onRequest(String srcIp, String destIp) {
        Request request = new Request();
        request.setSrcIp(srcIp);
        request.setDestIp(destIp);
        request.setTimestamp(new Date());
        request = requestRepo.save(request);

        ksession.insert(request);
        ksession.fireAllRules();

        return request;
    }
}
