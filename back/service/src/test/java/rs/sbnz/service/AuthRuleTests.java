package rs.sbnz.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.BlockReason;
import rs.sbnz.model.NoteType;
import rs.sbnz.model.events.BlockEvent;
import rs.sbnz.model.events.FailedLoginEvent;
import rs.sbnz.model.events.LoginEvent;
import rs.sbnz.model.events.Note;
import rs.sbnz.model.events.UnblockEvent;

class AuthRuleTests {
    @Test
    void properRemovalOfAuthBlock() {
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

        ksession.insert(new Note(10009L, attackerIp, 1L, NoteType.FAILED_LOGIN));
        int k = ksession.fireAllRules();
        assertEquals(1, k); // Block.

        // Unblock

        ksession.insert(new UnblockEvent(attackerIp));
        ksession.fireAllRules();

        // Insert a single note. It should not interfere with the previous ones,
        // because they have already been used to block the IP address once.

        ksession.insert(new Note(0L, attackerIp, 3L, NoteType.FAILED_LOGIN));
        k = ksession.fireAllRules();
        assertEquals(0, k);
    }

    @Test
    void tooManyFailedLogins() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        String attackerIp = "127.0.0.1";
        String otherIp = "192.168.0.1";
        String victimEmail = "bob@gmail.com";
        String otherEmail = "johhny@test.com";

        // --------------------------------------------------------------------
        // Won't activate: not enough failed logins in time. 
        // --------------------------------------------------------------------

        for (int i = 0; i < 4; i++) {
            ksession.insert(new FailedLoginEvent(Long.valueOf(i), attackerIp, victimEmail));
        }
        clock.advanceTime(1, TimeUnit.MINUTES);
        ksession.insert(new FailedLoginEvent(4L, attackerIp, victimEmail));

        int k = ksession.fireAllRules();
        assertEquals(0, k);

        
        // --------------------------------------------------------------------
        // Won't activate: different ip. 
        // --------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.HOURS);
        for (int i = 0; i < 4; i++) {
            ksession.insert(new FailedLoginEvent(Long.valueOf(i), attackerIp, victimEmail));
        }
        ksession.insert(new FailedLoginEvent(4L, otherIp, victimEmail));

        k = ksession.fireAllRules();
        assertEquals(0, k);

        // --------------------------------------------------------------------
        // Won't activate: not enough failed logins for the same email 
        // --------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.HOURS);
        for (int i = 0; i < 4; i++) {
            ksession.insert(new FailedLoginEvent(Long.valueOf(i), attackerIp, victimEmail));
        }
        ksession.insert(new FailedLoginEvent(4L, attackerIp, otherEmail));

        k = ksession.fireAllRules();
        assertEquals(0, k);

        // --------------------------------------------------------------------
        // Will activate
        // --------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.HOURS);
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

        ksession.insert(new Note(101L, attackerIp, 1000L, NoteType.FAILED_LOGIN));
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

    @Test
    void manyBlocksThereforeAuthAttack() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        // --------------------------------------------------------------------
        // Won't activate: They must be AUTH_ATTACK.
        // --------------------------------------------------------------------
        
        for (int i = 0; i < 99; i++) {
            String fakeIp = "" + i;
            ksession.insert(new BlockEvent(fakeIp, 24*60*60*1000L, BlockReason.AUTH_ATTACK));
        }
        ksession.insert(new BlockEvent("dhjkds", 24*60*60*1000L, BlockReason.TEMP));

        int k = ksession.fireAllRules();
        assertEquals(0, k);

        // --------------------------------------------------------------------
        // Won't activate: Must be 100 unique IPs blocked.
        // --------------------------------------------------------------------

        clock.advanceTime(2, TimeUnit.DAYS);

        for (int i = 0; i < 99; i++) {
            String fakeIp = "" + i;
            ksession.insert(new BlockEvent(fakeIp, 24*60*60*1000L, BlockReason.AUTH_ATTACK));
        }
        ksession.insert(new BlockEvent("0", 24*60*60*1000L, BlockReason.AUTH_ATTACK));

        k = ksession.fireAllRules();
        assertEquals(0, k);

        // --------------------------------------------------------------------
        // Will activate
        // --------------------------------------------------------------------

        clock.advanceTime(2, TimeUnit.DAYS);
        
        for (int i = 0; i < 100; i++) {
            String fakeIp = "" + i;
            ksession.insert(new BlockEvent(fakeIp, 24*60*60*1000L, BlockReason.AUTH_ATTACK));
        }

        k = ksession.fireAllRules();
        assertEquals(1, k);

        // --------------------------------------------------------------------
        // Won't activate: attack is in progress.
        // --------------------------------------------------------------------

        clock.advanceTime(1, TimeUnit.HOURS);
        
        for (int i = 0; i < 100; i++) {
            String fakeIp = "" + i;
            ksession.insert(new BlockEvent(fakeIp, 24*60*60*1000L, BlockReason.AUTH_ATTACK));
        }

        k = ksession.fireAllRules();
        assertEquals(0, k);
    }
}
