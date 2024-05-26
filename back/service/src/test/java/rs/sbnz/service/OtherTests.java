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

}
