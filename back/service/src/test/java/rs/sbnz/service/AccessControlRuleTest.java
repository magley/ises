package rs.sbnz.service;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.Request;

public class AccessControlRuleTest {
    @Test
    void corsAttackTest() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        String badPort = "10101";
        String goodPort = "5173";
        String externalIp = "135.246.98.89";
        String internalIp = "123.0.1.1";
    
        ksession.insert(new Request(1L, internalIp, "", goodPort));
        assertEquals(0, ksession.fireAllRules());

        ksession.insert(new Request(0L, internalIp, "", badPort));
        assertEquals(0, ksession.fireAllRules());

        ksession.insert(new Request(2L, externalIp, "", goodPort));
        assertEquals(0, ksession.fireAllRules());

        ksession.insert(new Request(3L, externalIp, "", badPort));
        assertEquals(1, ksession.fireAllRules());
    }
}
