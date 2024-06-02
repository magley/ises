package rs.sbnz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import rs.sbnz.model.BlockReason;
import rs.sbnz.model.Request;
import rs.sbnz.model.events.BlockEvent;
import rs.sbnz.model.events.DeleteStaleBlocksEvent;
import rs.sbnz.model.events.UnblockEvent;

public class BlockTest {
    @Test 
    void deletingStaleBlocks() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        for (int i = 0; i < 5; i++) {
            ksession.insert((new BlockEvent("123", (i + 1) * 1000L, BlockReason.TEMP)));
            clock.advanceTime(1, TimeUnit.MILLISECONDS);
        }
        ksession.fireAllRules();

        // --------------------------------------------------------------------
        // Only 1 block remains because it's not stale yet.
        // --------------------------------------------------------------------

        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new DeleteStaleBlocksEvent());
        ksession.fireAllRules();

        assertEquals(1, TestUtils.<BlockEvent>getFactsFrom(ksession, BlockEvent.class).size());
        assertEquals(0, TestUtils.<DeleteStaleBlocksEvent>getFactsFrom(ksession, DeleteStaleBlocksEvent.class).size());
    
        // --------------------------------------------------------------------
        // Everything is gone.
        // --------------------------------------------------------------------
        
        clock.advanceTime(3, TimeUnit.SECONDS);
        ksession.insert(new DeleteStaleBlocksEvent());
        ksession.fireAllRules();

        assertEquals(0, TestUtils.<BlockEvent>getFactsFrom(ksession, BlockEvent.class).size());
        assertEquals(0, TestUtils.<DeleteStaleBlocksEvent>getFactsFrom(ksession, DeleteStaleBlocksEvent.class).size());
    }

    @Test
    void gettingBlockEvents() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        for (int i = 0; i < 5; i++) {
            // 0th event will expire in 2s, other will expire in (2+e) seconds.
            ksession.insert((new BlockEvent("123", i * 10 + 2000L, BlockReason.TEMP)));
            clock.advanceTime(1, TimeUnit.MILLISECONDS);
        }
    
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.insert(new DeleteStaleBlocksEvent());
        ksession.fireAllRules();

        List<BlockEvent> blocks = new ArrayList<BlockEvent>();
        QueryResults results = ksession.getQueryResults("getAllBlockEventsForIp"); 
        for ( QueryResultsRow row : results ) {
            BlockEvent alarm = (BlockEvent) row.get( "$block" );
            blocks.add(alarm);
        }
        assertEquals(4, blocks.size()); // 5 minus stale block.
    }

    @Test
    void gettingBlockEvents2() {
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

        // --------------------------------------------------------------------
        // Request is rejected because of the IP block
        // --------------------------------------------------------------------

        clock.advanceTime(1, TimeUnit.SECONDS);
        Request r1 = new Request(1L, ip, "", "5173");
        ksession.insert(r1);
        ksession.fireAllRules();
        assertTrue(r1.getIsRejected());

        // --------------------------------------------------------------------
        // Request is not rejected because of the unblock event.
        // --------------------------------------------------------------------

        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new UnblockEvent(ip));
        ksession.fireAllRules();

        assertEquals(0, TestUtils.<BlockEvent>getFactsFrom(ksession, BlockEvent.class).size());
        assertEquals(0, TestUtils.<UnblockEvent>getFactsFrom(ksession, UnblockEvent.class).size());

        clock.advanceTime(1, TimeUnit.SECONDS);
        Request r2 = new Request(2L, ip, "", "5173");
        ksession.insert(r2);
        ksession.fireAllRules();
        assertFalse(r2.getIsRejected());

    }

    @Test
    void unblockWhenThereIsNoBlock() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        String ip = "123";

        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new UnblockEvent(ip));
        int k = ksession.fireAllRules();
        assertEquals(1, k);

        assertEquals(0, TestUtils.<BlockEvent>getFactsFrom(ksession, BlockEvent.class).size());
        assertEquals(0, TestUtils.<UnblockEvent>getFactsFrom(ksession, UnblockEvent.class).size());
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

        // The rule won't fire without this (it can by any time unit, though).
        // Check common.drl for an explanation.
        clock.advanceTime(1, TimeUnit.SECONDS);

        Request r = new Request(1L, ip, "", "5173");
        ksession.insert(r);
        
        ksession.fireAllRules();
        assertTrue(r.getIsRejected());

        //----------------------------------------------------------------------
        // Won't get rejected - request is before block event.
        //----------------------------------------------------------------------

        clock.advanceTime(-1, TimeUnit.HOURS);
        r = new Request(1L, ip, "", "5173");
        ksession.insert(r);

        ksession.fireAllRules();
        assertFalse(r.getIsRejected());

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
