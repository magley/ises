package rs.sbnz.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import rs.sbnz.model.Alarm;
import rs.sbnz.model.AlarmRemove;
import rs.sbnz.model.AlarmSeverity;
import rs.sbnz.model.AlarmType;
import rs.sbnz.model.BlockReason;
import rs.sbnz.model.Request;
import rs.sbnz.model.events.BlockEvent;
import rs.sbnz.model.events.DeleteStaleBlocksEvent;
import rs.sbnz.model.events.UnblockEvent;

public class OtherTests {
    @Test
    void alarmTest() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        Alarm a1 = new Alarm(AlarmSeverity.HIGH, AlarmType.WEAK_PASSWORD, "");
        ksession.insert(a1);
        ksession.fireAllRules();
        assertEquals(1, TestUtils.<Alarm>getFactsFrom(ksession).stream().filter(a -> !a.isHandled()).count());
        assertEquals(0, TestUtils.<Alarm>getFactsFrom(ksession).stream().filter(a -> a.isHandled()).count());

        ksession.insert(new AlarmRemove("Wrong UUID"));
        ksession.fireAllRules();
        assertEquals(1, TestUtils.<Alarm>getFactsFrom(ksession).stream().filter(a -> !a.isHandled()).count());
        assertEquals(0, TestUtils.<Alarm>getFactsFrom(ksession).stream().filter(a -> a.isHandled()).count());

        ksession.insert(new AlarmRemove(a1.getUuid()));
        ksession.fireAllRules();
        assertEquals(0, TestUtils.<Alarm>getFactsFrom(ksession).stream().filter(a -> !a.isHandled()).count());
        assertEquals(1, TestUtils.<Alarm>getFactsFrom(ksession).stream().filter(a -> a.isHandled()).count());
    }

    @Test void deleteStaleBlockEvents() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        for (int i = 0; i < 5; i++) {
            ksession.insert((new BlockEvent("123", (i + 1) * 1000L, BlockReason.TEMP)));
            clock.advanceTime(1, TimeUnit.MILLISECONDS);
        }
        ksession.fireAllRules();

        clock.advanceTime(3, TimeUnit.SECONDS);
        ksession.insert(new DeleteStaleBlocksEvent());
        ksession.fireAllRules();

        clock.advanceTime(3, TimeUnit.SECONDS);
        ksession.insert(new DeleteStaleBlocksEvent());
        ksession.fireAllRules();
    }


    @Test void gettingBlockEvents() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();  
        
        ksession.insert((new BlockEvent("123", 60 * 1000L, BlockReason.TEMP)));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert((new BlockEvent("456", 120 * 1000L, BlockReason.INJECTION)));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert((new BlockEvent("789", 180 * 1000L, BlockReason.AUTH_ATTACK)));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.fireAllRules();
        
        List<BlockEvent> blocks = new ArrayList<BlockEvent>();
        QueryResults results = ksession.getQueryResults("getAllBlockEventsForIp"); 
        for ( QueryResultsRow row : results ) {
            BlockEvent block = (BlockEvent) row.get( "$block" );
            blocks.add(block);
        }

        assertEquals(3, blocks.size());
    }

    @Test
    void unblockUser() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        String ip = "123";
        ksession.insert((new BlockEvent(ip, 60 * 1000L, BlockReason.TEMP)));
        ksession.fireAllRules();

        clock.advanceTime(1, TimeUnit.SECONDS);
        Request r1 = new Request(1L, ip, "", "5173");
        ksession.insert(r1);
        ksession.fireAllRules();
        assertTrue(r1.getIsRejected());

        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new UnblockEvent(ip));
        ksession.fireAllRules();

        clock.advanceTime(1, TimeUnit.SECONDS);
        Request r2 = new Request(2L, ip, "", "5173");
        ksession.insert(r2);
        ksession.fireAllRules();
        assertFalse(r2.getIsRejected());

        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new UnblockEvent(ip));
        int k = ksession.fireAllRules();
        assertEquals(1, k); // unblock when there's no block => "no block exists" rule gets fired
    }

    @Test
    void IfUserIsSpamming_ThenCreateNote() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        String ip = "1.2.3.4";
 
        //----------------------------------------------------------------------
        // Won't activate: Not enough requests in period of time
        //----------------------------------------------------------------------

        for (int i = 0; i < 50; i++) {
            ksession.insert(new Request(i + 0L, ip, "", "5173"));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        int k = ksession.fireAllRules();
        assertEquals(0, k);

        //----------------------------------------------------------------------
        // Will activate.
        //----------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.MINUTES);
        for (int i = 0; i < 50; i++) {
            ksession.insert(new Request(i + 0L, ip, "", "5173"));
        }
        k = ksession.fireAllRules();
        assertEquals(1, k);

        //----------------------------------------------------------------------
        // Won't activate: not enough time has passed since last note.
        //----------------------------------------------------------------------

        clock.advanceTime(1, TimeUnit.SECONDS);
        for (int i = 0; i < 50; i++) {
            ksession.insert(new Request(i + 0L, ip, "", "5173"));
        }
        k = ksession.fireAllRules();
        assertEquals(0, k);

        //----------------------------------------------------------------------
        // Will activate.
        //----------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.SECONDS);
        for (int i = 0; i < 50; i++) {
            ksession.insert(new Request(i + 0L, ip, "", "5173"));
        }
        k = ksession.fireAllRules();
        assertEquals(1, k);

    }

    @Test
    void whenRequestIsMadeWithBlockedIp_ThenItWontPass() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        String ip = "999.0.0.1";

        ksession.insert(new BlockEvent(ip, 24 * 60 * 60 * 1000L, BlockReason.AUTH_ATTACK));

        //----------------------------------------------------------------------
        // Will get rejected.
        //----------------------------------------------------------------------

        // The rule won't fire without this (it can by any time unit, really).
        // Check common.drl for an explanation.
        clock.advanceTime(1, TimeUnit.SECONDS);

        Request r = new Request(1L, ip, "", "5173");
        ksession.insert(r);
        ksession.fireAllRules();
        assertTrue(r.getIsRejected());

        //----------------------------------------------------------------------
        // Won't get rejected - block expired.
        //----------------------------------------------------------------------

        clock.advanceTime(10, TimeUnit.DAYS);
        Request r2= new Request(2L, ip, "", "5173");
        ksession.insert(r2);
      
        ksession.fireAllRules();
        assertFalse(r2.getIsRejected());
    }
}
