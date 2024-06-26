package rs.sbnz.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.NoteType;
import rs.sbnz.model.Request;
import rs.sbnz.model.api.Packet;
import rs.sbnz.model.events.Note;
import rs.sbnz.model.events.TextQueryEvent;
import rs.sbnz.model.events.UnblockEvent;

public class InjectionRuleTests {
    @Test
    void detectSQLInjection() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
 
        Request r1 = new Request(1L, "123", "dest", "5173");
        ksession.insert(r1);
        ksession.insert(new TextQueryEvent("' or 1=1;", r1));
        assertEquals(1, ksession.fireAllRules());
        
        Request r2 = new Request(2L, "123", "dest", "5173");
        ksession.insert(r2);
        ksession.insert(new TextQueryEvent("not an injection", r2));
        assertEquals(0, ksession.fireAllRules());
    }
    
    @Test
    void properRemovalOfSqlInjectionBlock() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();
        String ip = "1.3.5.7";

        for (int i = 0; i < 5; i++) {
            ksession.insert(new Note(1L, ip, 5L, NoteType.INJECTION));
            clock.advanceTime(2, TimeUnit.MINUTES);
        }
        assertEquals(1, ksession.fireAllRules());

        ksession.insert(new UnblockEvent(ip));
        ksession.fireAllRules();

        ksession.insert(new Note(1L, ip, 5L, NoteType.INJECTION));
        int k = ksession.fireAllRules();
        assertEquals(0, k);
    }

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

    @Test
    void getBlockedFromSqlInjections() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        String ip = "1.3.5.7";

        // --------------------------------------------------------------------
        // Will activate.
        // --------------------------------------------------------------------

        for (int i = 0; i < 5; i++) {
            ksession.insert(new Note(1L, ip, 5L, NoteType.INJECTION));
            clock.advanceTime(2, TimeUnit.MINUTES);
        }
        assertEquals(1, ksession.fireAllRules());

        // --------------------------------------------------------------------
        // Won't activate: block already exists.
        // --------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.MINUTES);

        for (int i = 0; i < 5; i++) {
            ksession.insert(new Note(1L, ip, 5L, NoteType.INJECTION));
            clock.advanceTime(2, TimeUnit.MINUTES);
        }
        assertEquals(0, ksession.fireAllRules());

        // --------------------------------------------------------------------
        // Will activate.
        // --------------------------------------------------------------------

        clock.advanceTime(30, TimeUnit.MINUTES);

        for (int i = 0; i < 5; i++) {
            ksession.insert(new Note(1L, ip, 5L, NoteType.INJECTION));
            clock.advanceTime(2, TimeUnit.MINUTES);
        }

        // Multiple blocks at the same time because we fireAllRules with
        // multiple notes of the same type for the same user. In the real world,
        // this would never happen.
        assertTrue(ksession.fireAllRules() >= 1);
    }
}
