package rs.sbnz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;
import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import rs.sbnz.model.Request;

class ServiceApplicationTests {
    @Test
    void setupKjarTest() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();
        int firedRules = 0;

        // --------------------------------------------------------------------
        // Won't activate because not enough requests have been submitted 
        // --------------------------------------------------------------------

        for (int i = 0; i < 999; i++) {
            ksession.insert(new Request(Long.valueOf(i), "141.212.12.8", "141.212.12.8"));
        }
        firedRules = ksession.fireAllRules();
        assertEquals(0, firedRules);

        // --------------------------------------------------------------------
        // Will activate
        // --------------------------------------------------------------------

        clock.advanceTime(5, TimeUnit.MINUTES);
        
        for (int i = 0; i < 1000; i++) {
            ksession.insert(new Request(Long.valueOf(i), "141.212.12.8", "141.212.12.8"));
        }
        firedRules = ksession.fireAllRules();
        assertEquals(1, firedRules);
    }
}
