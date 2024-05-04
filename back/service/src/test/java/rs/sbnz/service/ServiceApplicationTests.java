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

        Request req1 = new Request(0L, "141.212.12.8", "38.27.110.3");
        Request req2 = new Request(1L, "38.27.110.3", "38.27.110.3");
        Request req3 = new Request(2L, "131.1.0.34", "12.178.192.8");

        ksession.insert(req1);
        ksession.insert(req2);
        ksession.insert(req3);
        firedRules = ksession.fireAllRules();
        clock.advanceTime(9000, TimeUnit.MINUTES);

        assertEquals(1, firedRules);
    }
}
