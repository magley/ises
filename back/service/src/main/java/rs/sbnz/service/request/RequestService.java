package rs.sbnz.service.request;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.sbnz.model.BlockReason;
import rs.sbnz.model.Request;
import rs.sbnz.model.api.Packet;
import rs.sbnz.model.events.BlockEvent;
import rs.sbnz.model.events.FailedLoginEvent;
import rs.sbnz.model.events.LoginEvent;
import rs.sbnz.service.exceptions.IPBlockedException;

@Component
public class RequestService {
    @Autowired private KieSession ksession;
    @Autowired private IRequestRepo requestRepo;

    /**
     * Method to call on start of every HTTP Request made to the server.
     * Automatically submits the base request packet to the rule engine.
     * 
     * @param packet Request packet. 
     * @return The Request entity.
     */
    public Request onRequest(Packet packet) {
        Request request = new Request(0L, packet);
        request = requestRepo.save(request);

        ksession.insert(request);
        ksession.fireAllRules();

        if (request.getIsRejected()) {
            throw new IPBlockedException();
        }

        return request;
    }

    public void onLoginAttempt(String email, String password) {
        LoginEvent ev = new LoginEvent(email, password);
        ksession.insert(ev);
        ksession.fireAllRules();
    }

    /**
     * Method to call when the user enters incorrect credentails for logging in.
     * TODO: This method may not belong here. Rename the class or split it.
     * 
     * @param srcIp IP address from where the request came.
     * @param email Email address that the user attempted to enter.
     */
    public void onFailedLogin(String srcIp, String email) {
        FailedLoginEvent event = new FailedLoginEvent(0L, srcIp, email);
        ksession.insert(event);
        ksession.fireAllRules();
    }

    /**
     * Temporary method to forcibly block an IP address.
     * 
     * @param srcIp IP address to block.
     * @param durationMs Duration (in ms) for how long the block should last.
     * @return The BlockEvent.
     */
    public BlockEvent TEMP_force_block_ip(String srcIp, Long durationMs) {
        BlockEvent event = new BlockEvent(srcIp, durationMs, BlockReason.AUTH_ATTACK);
        ksession.insert(event);
        ksession.fireAllRules();
        return event;
    }
}
