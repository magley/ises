package rs.sbnz.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.events.LoginEvent;
import rs.sbnz.model.Alarm;
import rs.sbnz.model.AlarmRemove;
import rs.sbnz.model.AlarmType;
import rs.sbnz.model.WeakPassword;

public class WeakPasswordTest {
    @Test
    void tooManyAccountsLoggedInWithSamePassword_weakPassword() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        for (int i = 0; i < 4; i++) {
            ksession.insert(new LoginEvent(i + "@gmail.com", "123"));
            clock.advanceTime(1, TimeUnit.MINUTES);
        }

        LoginEvent ev = new LoginEvent("@gmail.com", "123");
        ksession.insert(ev);

        ksession.fireAllRules();
        assertTrue(ev.getWeakPassword());
    }

    private long getCount_Unhandled_WeakPassword_Alarms(KieSession ksession) {
        return TestUtils.<Alarm>getFactsFrom(ksession)
            .stream()
            .filter(a -> !a.isHandled() && a.getType() == AlarmType.WEAK_PASSWORD)
            .count();
    }

    @Test
    void tooManyWeakPasswords_alarm() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        long totalAlarms = 0;

        // --------------------------------------------------------------------
        // New alarm is added.
        // --------------------------------------------------------------------

        for (int i = 0; i < 50; i++) {
            ksession.insert(new WeakPassword(i + "_pass"));
        }

        ksession.fireAllRules();
        totalAlarms = getCount_Unhandled_WeakPassword_Alarms(ksession);
        assertEquals(1, totalAlarms);
        
        // --------------------------------------------------------------------
        // Handle alarm -> 0 active weak password alarms.
        // --------------------------------------------------------------------

        Alarm alarm = TestUtils.<Alarm>getFactsFrom(ksession).get(0);
        System.out.println(alarm.getUuid());
        ksession.insert(new AlarmRemove(alarm.getUuid()));
        ksession.fireAllRules();
        totalAlarms = getCount_Unhandled_WeakPassword_Alarms(ksession);
        assertEquals(0, totalAlarms);

        // --------------------------------------------------------------------
        // Not enough time has passed to insert a new alarm.
        // --------------------------------------------------------------------

        for (int i = 0; i < 50; i++) {
            ksession.insert(new WeakPassword(i + "_pass"));
        }

        ksession.fireAllRules();
        totalAlarms = getCount_Unhandled_WeakPassword_Alarms(ksession);
        assertEquals(0, totalAlarms);

        
        // --------------------------------------------------------------------
        // New alarm is inserted.
        // --------------------------------------------------------------------

        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime(1, TimeUnit.HOURS);

        for (int i = 0; i < 50; i++) {
            ksession.insert(new WeakPassword(i + "_pass"));
        }

        ksession.fireAllRules();
        totalAlarms = getCount_Unhandled_WeakPassword_Alarms(ksession);
        assertEquals(1, totalAlarms);
    }
}
