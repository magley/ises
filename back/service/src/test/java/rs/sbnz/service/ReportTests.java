package rs.sbnz.service;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.core.time.SessionPseudoClock;
import org.drools.template.objects.ArrayDataProvider;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.utils.KieHelper;

import rs.sbnz.model.AttackSeverity;
import rs.sbnz.model.AttackType;
import rs.sbnz.model.BlockReason;
import rs.sbnz.model.events.AttackEvent;
import rs.sbnz.model.events.BlockEvent;
import rs.sbnz.model.events.FailedLoginEvent;

public class ReportTests {
    @Test
    void attackEventReport() {
        KieSession ksession = createKSessionWithStreamProcessingAndPseudoClockFromTemplate();
        SessionPseudoClock clock = ksession.getSessionClock();

        // Won't print anything
        for (int i = 0; i < 2; ++i) {
            ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.HIGH));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports").setFocus();
        int k1 = ksession.fireAllRules();
        assertEquals(0, k1);

        clock.advanceTime(3, TimeUnit.DAYS);

        // Will print the 5 attacks
        for (int i = 0; i < 5; ++i) {
            ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.HIGH));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports").setFocus();
        int k2 = ksession.fireAllRules();
        assertEquals(5, k2);
    }

    @Test
    void variedAttackEventReport() {
        KieSession ksession = createKSessionWithStreamProcessingAndPseudoClockFromTemplate();
        SessionPseudoClock clock = ksession.getSessionClock();

        // Won't print anything
        for (int i = 0; i < 2; ++i) {
            ksession.insert(new AttackEvent(AttackType.AUTHENTICATION, AttackSeverity.HIGH));
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
            ksession.insert(new AttackEvent(type, AttackSeverity.HIGH));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ksession.getAgenda().getAgendaGroup("reports2").setFocus();
        int k2 = ksession.fireAllRules();
        assertEquals(5, k2);
    }

    @Test
    void failedLoginAttemptReport() {
        KieSession ksession = createKSessionWithStreamProcessingAndPseudoClockFromTemplate();
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
        KieSession ksession = createKSessionWithStreamProcessingAndPseudoClockFromTemplate();
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

    // Parts copied from lab materials, then frakensteined to support stream mode with pseudo clocks
    private KieSession createKSessionWithStreamProcessingAndPseudoClockFromTemplate(){
        InputStream template = ReportTests.class.getResourceAsStream("/reports/reportRules.drl");

        DataProvider dataProvider = new ArrayDataProvider(new String[][]{
            // Data to fill out template
            new String[]{"AttackType.AUTHENTICATION", "3"}
        });

        DataProviderCompiler converter = new DataProviderCompiler();
        String drl = converter.compile(dataProvider, template);
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);
        
        Results results = kieHelper.verify();
        
        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.println("Error: "+message.getText());
            }
            
            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }
        
        KieServices ks = KieServices.Factory.get();

        KieBaseConfiguration kbconf = ks.newKieBaseConfiguration();
        kbconf.setOption(EventProcessingOption.STREAM);

        KieSessionConfiguration ksconf = ks.newKieSessionConfiguration();
        ksconf.setOption(ClockTypeOption.get("pseudo"));

        return kieHelper.build(kbconf).newKieSession(ksconf, null);
    }
}
