package rs.sbnz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.Request;

public class DosRuleTests {
    @Test
    void tcpFlood() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();
        int firedRules = 0;

        // --------------------------------------------------------------------
        // Won't activate: not enough requests in the required time 
        // --------------------------------------------------------------------
        
        clock.advanceTime(5, TimeUnit.MINUTES);
        for (int i = 0; i < 999; i++) {
            ksession.insert(new Request(Long.valueOf(i), "141.212.12.8", "141.212.12.8", "5173"));
        }

        clock.advanceTime(1, TimeUnit.MINUTES);
        ksession.insert(new Request(999L, "141.212.12.8", "141.212.12.8", "5173"));

        firedRules = ksession.fireAllRules();
        assertEquals(0, firedRules);

        // --------------------------------------------------------------------
        // Will activate
        // --------------------------------------------------------------------

        clock.advanceTime(5, TimeUnit.MINUTES);
        
        for (int i = 0; i < 1000; i++) {
            ksession.insert(new Request(Long.valueOf(i), "141.212.12.8", "141.212.12.8", "5173"));
        }
        firedRules = ksession.fireAllRules();
        assertEquals(1, firedRules);
    }
}
