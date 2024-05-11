package rs.sbnz.service;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.AttackType;
import rs.sbnz.model.events.AttackEvent;

public class CompoundAttacksTest {
    @Test
    void twoDifferentAttacks() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        // ---------------------------------------------------------------------
        // Will activate
        // ---------------------------------------------------------------------

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION));
        clock.advanceTime(1, TimeUnit.SECONDS); // One attack must be "after" another.
        ksession.insert(new AttackEvent(AttackType.DENIAL_OF_SERVICE));
        int k = ksession.fireAllRules();
        assertEquals(1, k);

        // ---------------------------------------------------------------------
        // Won't activate: one attack has expired.
        // ---------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.DAYS);

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION));
        clock.advanceTime(10, TimeUnit.DAYS);
        ksession.insert(new AttackEvent(AttackType.DENIAL_OF_SERVICE));
        k = ksession.fireAllRules();
        assertEquals(0, k);

        // ---------------------------------------------------------------------
        // Won't activate: attacks are of same type.
        // ---------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.DAYS);

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION));
        k = ksession.fireAllRules();
        assertEquals(0, k);
    }
}
