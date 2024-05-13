package rs.sbnz.service;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.AttackType;
import rs.sbnz.model.BlockReason;
import rs.sbnz.model.events.AttackEvent;
import rs.sbnz.model.events.BlockEvent;
import rs.sbnz.model.events.FailedLoginEvent;

public class ReportTests {
    @Test
    void attackEventReport() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("reportsPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        // Won't print anything
        for (int i = 0; i < 2; ++i) {
            ksession.insert(new AttackEvent(AttackType.AUTHENTICATION));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports").setFocus();
        int k1 = ksession.fireAllRules();
        assertEquals(0, k1);

        clock.advanceTime(3, TimeUnit.DAYS);

        // Will print the 5 attacks
        for (int i = 0; i < 5; ++i) {
            ksession.insert(new AttackEvent(AttackType.AUTHENTICATION));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports").setFocus();
        int k2 = ksession.fireAllRules();
        assertEquals(5, k2);
    }

    @Test
    void variedAttackEventReport() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("reportsPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        // Won't print anything
        for (int i = 0; i < 2; ++i) {
            ksession.insert(new AttackEvent(AttackType.AUTHENTICATION));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports2").setFocus();
        int k1 = ksession.fireAllRules();
        assertEquals(0, k1);

        clock.advanceTime(3, TimeUnit.DAYS);

        // Will print the 5 attacks
        AttackType types[] = { AttackType.AUTHENTICATION, AttackType.ACCESS_CONTROL, AttackType.DENIAL_OF_SERVICE, AttackType.INJECTION, AttackType.INJECTION };
        for (int i = 0; i < 5; ++i) {
            AttackType type = types[i];
            ksession.insert(new AttackEvent(type));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports2").setFocus();
        int k2 = ksession.fireAllRules();
        assertEquals(5, k2);
    }

    @Test
    void failedLoginAttemptReport() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("reportsPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        // Won't print anything
        for (int i = 0; i < 2; ++i) {
            String ip = "135.246.98.89";
            String email = "user" + i + "@email.com";
            ksession.insert(new FailedLoginEvent((long)i, ip, email));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports").setFocus();
        int k1 = ksession.fireAllRules();
        assertEquals(0, k1);

        clock.advanceTime(3, TimeUnit.DAYS);

        // Will print the 3 events
        for (int i = 0; i < 3; ++i) {
            String ip = "135.246.98.89";
            String email = "user" + i + "@email.com";
            ksession.insert(new FailedLoginEvent((long)i, ip, email));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports").setFocus();
        int k2 = ksession.fireAllRules();
        assertEquals(3, k2);

        clock.advanceTime(3, TimeUnit.DAYS);

        // Will print the 3 events with same ip (135.246.98.89)
        String ips[] = {"135.246.98.89", "135.246.98.89", "135.246.98.89", "135.246.98.90", "135.246.98.90"};
        for (int i = 0; i < 5; ++i) {
            String ip = ips[i];
            String email = "user" + i + "@email.com";
            ksession.insert(new FailedLoginEvent((long)i, ip, email));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports").setFocus();
        int k3 = ksession.fireAllRules();
        assertEquals(3, k3);
    }

    @Test
    void blockEventReport() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("reportsPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        // Won't print anything
        for (int i = 0; i < 2; ++i) {
            String ip = "135.246.98.89";
            ksession.insert(new BlockEvent(ip, 1000L, BlockReason.AUTH_ATTACK));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports").setFocus();
        int k1 = ksession.fireAllRules();
        assertEquals(0, k1);

        clock.advanceTime(3, TimeUnit.DAYS);

        // Will print the 3 events
        for (int i = 0; i < 3; ++i) {
            String ip = "135.246.98.89";
            ksession.insert(new BlockEvent(ip, 1000L, BlockReason.AUTH_ATTACK));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports").setFocus();
        int k2 = ksession.fireAllRules();
        assertEquals(3, k2);

        clock.advanceTime(3, TimeUnit.DAYS);

        // Will print the 3 events with same ip (135.246.98.89)
        String ips[] = {"135.246.98.89", "135.246.98.89", "135.246.98.89", "135.246.98.90", "135.246.98.90"};
        for (int i = 0; i < 5; ++i) {
            String ip = ips[i];
            ksession.insert(new BlockEvent(ip, 1000L, BlockReason.AUTH_ATTACK));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports").setFocus();
        int k3 = ksession.fireAllRules();
        assertEquals(3, k3);
    }
}
