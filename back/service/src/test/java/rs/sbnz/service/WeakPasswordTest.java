package rs.sbnz.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.events.LoginEvent;
import rs.sbnz.model.events.PasswordChangeEvent;
import rs.sbnz.model.Alarm;
import rs.sbnz.model.AlarmRemove;
import rs.sbnz.model.AlarmType;
import rs.sbnz.model.WeakPassword;

public class WeakPasswordTest {
    @Test
    void whenManyLoginsWithSamePassword_makePasswordWeak() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();

        // --------------------------------------------------------------------
        // Will pass.
        // --------------------------------------------------------------------

        for (int i = 0; i < 5; i++) {
            ksession.insert(new LoginEvent(i + "@gmail.com", "123"));
            clock.advanceTime(1, TimeUnit.MINUTES);
        }
        ksession.fireAllRules();

        assertEquals(1, TestUtils.<WeakPassword>getFactsFrom(ksession, WeakPassword.class).stream().filter(p -> p.getPassword().equals("123")).count());

        // --------------------------------------------------------------------
        // Won't activate: password is already weak.
        // --------------------------------------------------------------------

        for (int i = 0; i < 5; i++) {
            ksession.insert(new LoginEvent(i + "@gmail.com", "123"));
            clock.advanceTime(1, TimeUnit.MINUTES);
        }
        ksession.fireAllRules();
        // Still equals 1
        assertEquals(1, TestUtils.<WeakPassword>getFactsFrom(ksession, WeakPassword.class).stream().filter(p -> p.getPassword().equals("123")).count());
    }

    @Test
    void whenLoginWeakPassword_nagUser() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        // --------------------------------------------------------------------
        // Will pass.
        // --------------------------------------------------------------------

        ksession.insert(new WeakPassword("123"));
        LoginEvent ev = new LoginEvent("@gmail.com", "123");
        ksession.insert(ev);
        ksession.fireAllRules();
        assertTrue(ev.getWeakPassword());

        // --------------------------------------------------------------------
        // Won't pass: not a weak password.
        // --------------------------------------------------------------------

        ev = new LoginEvent("@gmail.com", "cyNVRPLC_00860");
        ksession.insert(ev);
        ksession.fireAllRules();
        assertFalse(ev.getWeakPassword());
    }

    @Test
    void flagPasswordChangeEvent_if_newPasswordIsWeak() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        ksession.insert(new WeakPassword("123"));
        ksession.fireAllRules();

        // --------------------------------------------------------------------
        // Will pass.
        // --------------------------------------------------------------------

        PasswordChangeEvent pce1 = new PasswordChangeEvent("123");
        ksession.insert(pce1);
        ksession.fireAllRules();
        assertTrue(pce1.getIsWeak());
        
        // --------------------------------------------------------------------
        // Won't pass: not a weak password.
        // --------------------------------------------------------------------

        PasswordChangeEvent pce2 = new PasswordChangeEvent("cyNVRPLC_00860");
        ksession.insert(pce2);
        ksession.fireAllRules();
        assertFalse(pce2.getIsWeak());
    }


    private long getCount_Unhandled_WeakPassword_Alarms(KieSession ksession) {
        return TestUtils.<Alarm>getFactsFrom(ksession, Alarm.class)
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

        Alarm alarm = TestUtils.<Alarm>getFactsFrom(ksession, Alarm.class).get(0);
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
