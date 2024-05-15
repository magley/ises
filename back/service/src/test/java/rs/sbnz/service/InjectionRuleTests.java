package rs.sbnz.service;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.Request;
import rs.sbnz.model.api.Packet;
import rs.sbnz.model.events.TextQueryEvent;

public class InjectionRuleTests {
    @Test
    void sqlInjectionTest() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        String ip = "1.3.5.7";

        // --------------------------------------------------------------------
        // Will activate.
        // --------------------------------------------------------------------

        Request req01 = new Request(0L, new Packet(ip, "", "5173"));
        TextQueryEvent tex01 = new TextQueryEvent("' or 1=1;", req01);

        ksession.insert(req01);
        ksession.insert(tex01);
        int k = ksession.fireAllRules();
        assertEquals(1, k);

        // --------------------------------------------------------------------
        // Will activate.
        // --------------------------------------------------------------------

        clock.advanceTime(1, TimeUnit.MINUTES);

        Request req02 = new Request(1L, new Packet(ip, "", "5173"));
        TextQueryEvent tex02 = new TextQueryEvent("      ', DROP TABLE user; ", req02);

        ksession.insert(req02);
        ksession.insert(tex02);
        k = ksession.fireAllRules();
        assertEquals(1, k);
        
        // --------------------------------------------------------------------
        // Won't activate: Query does not start with a '
        // --------------------------------------------------------------------

        clock.advanceTime(1, TimeUnit.MINUTES);

        Request req03 = new Request(1L, new Packet(ip, "", "5173"));
        TextQueryEvent tex03 = new TextQueryEvent("    DROP TABLE permissions;    ", req03);

        ksession.insert(req03);
        ksession.insert(tex03);
        k = ksession.fireAllRules();
        assertEquals(0, k);
    }
}
