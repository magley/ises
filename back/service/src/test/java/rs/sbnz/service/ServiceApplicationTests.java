package rs.sbnz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;
import org.drools.core.time.SessionPseudoClock;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.Permission;
import rs.sbnz.model.Request;
import rs.sbnz.model.Role;
import rs.sbnz.model.User;

class ServiceApplicationTests {
    @Test
    void setupKjarTest() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");
        SessionPseudoClock clock = ksession.getSessionClock();
        int firedRules = 0;

        // --------------------------------------------------------------------
        // Won't activate because not enough requests have been submitted 
        // --------------------------------------------------------------------

        for (int i = 0; i < 999; i++) {
            ksession.insert(new Request(Long.valueOf(i), "141.212.12.8", "141.212.12.8"));
        }
        firedRules = ksession.fireAllRules();
        assertEquals(0, firedRules);

        // --------------------------------------------------------------------
        // Will activate
        // --------------------------------------------------------------------

        clock.advanceTime(5, TimeUnit.MINUTES);
        
        for (int i = 0; i < 1000; i++) {
            ksession.insert(new Request(Long.valueOf(i), "141.212.12.8", "141.212.12.8"));
        }
        firedRules = ksession.fireAllRules();
        assertEquals(1, firedRules);
    }

    @Test
    void testRbacRole() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        // --------------------------------------------------------------------
        // Create permissions and roles.
        // --------------------------------------------------------------------

        Permission p1 = new Permission(1L, "comment_on_articles");
        Permission p2 = new Permission(2L, "buy_article");
        Permission p3 = new Permission(3L, "sell_article");
        Permission p4 = new Permission(4L, "ban_user");
        Permission p5 = new Permission(5L, "unlock_system");

        Role rBasicClient = new Role(1L, "Basic client", "");
        Role rClient = new Role(2L, "Client", "");
        Role rAdmin = new Role(3L, "Admin", "");
        Role rSuperAdmin = new Role(4L, "Super Admin", "");

        rBasicClient.getPermissions().add(p1);
        rBasicClient.getPermissions().add(p2);

        rClient.setParent(rBasicClient);
        rClient.getPermissions().add(p3);

        rAdmin.getPermissions().add(p1);
        rAdmin.getPermissions().add(p4);

        rSuperAdmin.setParent(rAdmin);
        rSuperAdmin.getPermissions().add(p5);

        // --------------------------------------------------------------------
        // Create users with roles.
        // --------------------------------------------------------------------

        User u1 = new User(1L);
        User u2 = new User(2L);
        User u3 = new User(3L);
        User u4 = new User(4L);

        u1.setRbacRole(rBasicClient);
        u2.setRbacRole(rClient);
        u3.setRbacRole(rAdmin);
        u4.setRbacRole(rSuperAdmin);

        // --------------------------------------------------------------------
        // Push everything into the knowledge base.
        // --------------------------------------------------------------------

        ksession.insert(p1);
        ksession.insert(p2);
        ksession.insert(p3);
        ksession.insert(p4);
        ksession.insert(p5);
        
        ksession.insert(rBasicClient);
        ksession.insert(rClient);
        ksession.insert(rAdmin);
        ksession.insert(rSuperAdmin);

        ksession.insert(u1);
        ksession.insert(u2);
        ksession.insert(u3);
        ksession.insert(u4);
        ksession.fireAllRules();

        // Debug console should print out something like this:
        /*
            User with id 4 has permission 4 (ban_user)
            User with id 4 has permission 1 (comment_on_articles)
            User with id 2 has permission 2 (buy_article)
            User with id 2 has permission 1 (comment_on_articles)
            User with id 4 has permission 5 (unlock_system)
            User with id 3 has permission 4 (ban_user)
            User with id 3 has permission 1 (comment_on_articles)
            User with id 2 has permission 3 (sell_article)
            User with id 1 has permission 2 (buy_article)
            User with id 1 has permission 1 (comment_on_articles)
        */
    }
}
