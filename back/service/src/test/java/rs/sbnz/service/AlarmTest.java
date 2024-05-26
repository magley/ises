package rs.sbnz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

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

public class AlarmTest {
    @Test
    void alarmQueryTest() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        // --------------------------------------------------------------------
        // Unhandled alarms will be returned by the query.
        // --------------------------------------------------------------------

        ksession.insert(new Alarm(AlarmSeverity.CRITICAL, AlarmType.DOS, "a"));
        ksession.insert(new Alarm(AlarmSeverity.CRITICAL, AlarmType.INJECTION, "b"));
        ksession.insert(new Alarm(AlarmSeverity.CRITICAL, AlarmType.LOGIN_BREACH, "c"));

        // --------------------------------------------------------------------
        // Handled alarms won't be returned by the query.
        // --------------------------------------------------------------------

        Alarm handled1 = new Alarm(AlarmSeverity.CRITICAL, AlarmType.TARGETED_ATTACK, "d");
        handled1.setHandled(true);
        ksession.insert(handled1);
        Alarm handled2 = new Alarm(AlarmSeverity.CRITICAL, AlarmType.WEAK_PASSWORD, "e");
        handled2.setHandled(true);
        ksession.insert(handled2);


        List<Alarm> alarms = new ArrayList<Alarm>();
        QueryResults results = ksession.getQueryResults("getAllUnhandledAlarms"); 
        for ( QueryResultsRow row : results ) {
            Alarm alarm = (Alarm) row.get( "$alarm" );
            alarms.add(alarm);
        }
        assertEquals(3, alarms.size());
    }

    @Test
    void alarmRemoveTest() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        // --------------------------------------------------------------------
        // Alarm is added.
        // --------------------------------------------------------------------

        Alarm a1 = new Alarm(AlarmSeverity.HIGH, AlarmType.WEAK_PASSWORD, "");
        ksession.insert(a1);
        ksession.fireAllRules();
        assertEquals(1, TestUtils.<Alarm>getFactsFrom(ksession, Alarm.class).stream().filter(a -> !a.isHandled()).count());
        assertEquals(0, TestUtils.<Alarm>getFactsFrom(ksession, Alarm.class).stream().filter(a -> a.isHandled()).count());

        // --------------------------------------------------------------------
        // AlarmRemove for different alarm -> Nothing changes.
        // --------------------------------------------------------------------

        ksession.insert(new AlarmRemove("Wrong UUID"));
        ksession.fireAllRules();
        assertEquals(1, TestUtils.<Alarm>getFactsFrom(ksession, Alarm.class).stream().filter(a -> !a.isHandled()).count());
        assertEquals(0, TestUtils.<Alarm>getFactsFrom(ksession, Alarm.class).stream().filter(a -> a.isHandled()).count());

        // --------------------------------------------------------------------
        // AlarmRemove -> alarm is handled.
        // --------------------------------------------------------------------

        ksession.insert(new AlarmRemove(a1.getUuid()));
        ksession.fireAllRules();
        assertEquals(0, TestUtils.<Alarm>getFactsFrom(ksession, Alarm.class).stream().filter(a -> !a.isHandled()).count());
        assertEquals(1, TestUtils.<Alarm>getFactsFrom(ksession, Alarm.class).stream().filter(a -> a.isHandled()).count());
    }

}
