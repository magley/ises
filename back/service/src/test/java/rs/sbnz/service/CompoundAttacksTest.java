package rs.sbnz.service;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.AttackSeverity;
import rs.sbnz.model.AttackType;
import rs.sbnz.model.events.AttackEvent;

public class CompoundAttacksTest {
    @Test
    void singleCriticalAttack() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        // --------------------------------------------------------------------
        // Won't activate - not critical or above.
        // --------------------------------------------------------------------

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.SUPERHIGH));
        int k = ksession.fireAllRules();
        assertEquals(0, k);

        ksession = kContainer.newKieSession("ksessionPseudoClock"); // Have to reset to prevent other compund attacks from firing.

        // --------------------------------------------------------------------
        // Will activate
        // --------------------------------------------------------------------

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.CRITICAL));
        k = ksession.fireAllRules();
        assertEquals(1, k);
    }

    @Test
    void fourDifferentAttacks() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        // --------------------------------------------------------------------
        // Will activate
        // --------------------------------------------------------------------

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.LOW));
        ksession.insert(new AttackEvent(AttackType.DENIAL_OF_SERVICE, AttackSeverity.LOW));
        ksession.insert(new AttackEvent(AttackType.ACCESS_CONTROL, AttackSeverity.LOW));
        ksession.insert(new AttackEvent(AttackType.INJECTION, AttackSeverity.LOW));
        int k = ksession.fireAllRules();
        assertEquals(1, k);

        // --------------------------------------------------------------------
        // Won't activate: The all must be of different type.
        // --------------------------------------------------------------------

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.LOW));
        ksession.insert(new AttackEvent(AttackType.DENIAL_OF_SERVICE, AttackSeverity.LOW));
        ksession.insert(new AttackEvent(AttackType.INJECTION, AttackSeverity.LOW));
        ksession.insert(new AttackEvent(AttackType.INJECTION, AttackSeverity.LOW));
        k = ksession.fireAllRules();
        assertEquals(0, k);
    }

    @Test
    void twoDifferentAttacksSuperHighOrAbove() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        // ---------------------------------------------------------------------
        // Will activate
        // ---------------------------------------------------------------------

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.SUPERHIGH));
        clock.advanceTime(1, TimeUnit.SECONDS); // One attack must be "after" another.
        ksession.insert(new AttackEvent(AttackType.DENIAL_OF_SERVICE, AttackSeverity.CRITICAL));
        int k = ksession.fireAllRules();
        assertEquals(1, k);

        // ---------------------------------------------------------------------
        // Won't activate: one of the attacks has insufficient severity.
        // ---------------------------------------------------------------------

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.SUPERHIGH));
        clock.advanceTime(1, TimeUnit.SECONDS); // One attack must be "after" another.
        ksession.insert(new AttackEvent(AttackType.DENIAL_OF_SERVICE, AttackSeverity.LOW));
        k = ksession.fireAllRules();
        assertEquals(0, k);

        // ---------------------------------------------------------------------
        // Won't activate: one attack has expired.
        // ---------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.DAYS);

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.SUPERHIGH));
        clock.advanceTime(10, TimeUnit.DAYS);
        ksession.insert(new AttackEvent(AttackType.DENIAL_OF_SERVICE, AttackSeverity.SUPERHIGH));
        k = ksession.fireAllRules();
        assertEquals(0, k);

        // ---------------------------------------------------------------------
        // Won't activate: attacks are of same type.
        // ---------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.DAYS);

        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.SUPERHIGH));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.SUPERHIGH));
        k = ksession.fireAllRules();
        assertEquals(0, k);
    }
}
