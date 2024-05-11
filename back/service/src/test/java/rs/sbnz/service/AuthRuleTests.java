package rs.sbnz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.NoteType;
import rs.sbnz.model.Request;
import rs.sbnz.model.events.FailedLoginEvent;
import rs.sbnz.model.events.Note;

class AuthRuleTests {
    @Test
    void tooManyFailedLogins() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        String attackerIp = "127.0.0.1";
        String victimEmail = "bob@gmail.com";
        String otherEmail = "johhny@test.com";

        // --------------------------------------------------------------------
        // Won't activate: not enough failed logins in time. 
        // --------------------------------------------------------------------

        for (int i = 0; i < 4; i++) {
            ksession.insert(new FailedLoginEvent(Long.valueOf(i), attackerIp, victimEmail));
        }
        clock.advanceTime(1, TimeUnit.MINUTES);
        ksession.insert(new Request(4L, attackerIp, victimEmail));

        int k = ksession.fireAllRules();
        assertEquals(0, k);

        // --------------------------------------------------------------------
        // Won't activate: not enough failed logins for the same email 
        // --------------------------------------------------------------------

        clock.advanceTime(5, TimeUnit.MINUTES);

        for (int i = 0; i < 4; i++) {
            ksession.insert(new FailedLoginEvent(Long.valueOf(i), attackerIp, victimEmail));
        }
        ksession.insert(new Request(4L, attackerIp, otherEmail));

        k = ksession.fireAllRules();
        assertEquals(0, k);

        // --------------------------------------------------------------------
        // Will activate
        // --------------------------------------------------------------------

        clock.advanceTime(5, TimeUnit.MINUTES);

        for (int i = 0; i < 5; i++) {
            ksession.insert(new FailedLoginEvent(Long.valueOf(i), attackerIp, victimEmail));
        }

        k = ksession.fireAllRules();
        assertEquals(1, k);

        // --------------------------------------------------------------------
        // Won't activate: Not enough time has passed since the last note
        // --------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.SECONDS);

        for (int i = 0; i < 5; i++) {
            ksession.insert(new FailedLoginEvent(Long.valueOf(i), attackerIp, victimEmail));
        }

        k = ksession.fireAllRules();
        assertEquals(0, k);

        // --------------------------------------------------------------------
        // Will activate
        // --------------------------------------------------------------------

        clock.advanceTime(1, TimeUnit.MINUTES);

        for (int i = 0; i < 5; i++) {
            ksession.insert(new FailedLoginEvent(Long.valueOf(i), attackerIp, victimEmail));
        }

        k = ksession.fireAllRules();
        assertEquals(1, k);
    }

    @Test
    void failedLoginNotesCauseBlock() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        String attackerIp = "127.0.0.1";
        String victimEmail = "bob@gmail.com";

        // --------------------------------------------------------------------
        // Will activate.
        // --------------------------------------------------------------------

        for (int i = 0; i < 33; i++) {
            for (int j = 0; j < 5; j++) {
                ksession.insert(new FailedLoginEvent(Long.valueOf(i * 100 + j), attackerIp, victimEmail));
            }

            int k = ksession.fireAllRules();
            assertEquals(1, k);
            clock.advanceTime(2, TimeUnit.MINUTES);
        }

        // At this point, there should be 33 notes with 3 points each. We're 1
        // point shy from getting the ip blocked.

        ksession.insert(new Note(100L, attackerIp, 1L, NoteType.FAILED_LOGIN));
        int k = ksession.fireAllRules();
        assertEquals(1, k); // Block.

        // --------------------------------------------------------------------
        // Won't activate: IP is already blocked.
        // --------------------------------------------------------------------

        clock.advanceTime(1, TimeUnit.HOURS); // Block lasts 24h.

        ksession.insert(new Note(101L, attackerIp, 100L, NoteType.FAILED_LOGIN));
        k = ksession.fireAllRules();
        assertEquals(0, k);

        // --------------------------------------------------------------------
        // Will activate: Previous block expired.
        // --------------------------------------------------------------------
        
        clock.advanceTime(24, TimeUnit.HOURS);

        ksession.insert(new Note(102L, attackerIp, 100L, NoteType.FAILED_LOGIN));
        k = ksession.fireAllRules();
        assertEquals(1, k);
    }
}
