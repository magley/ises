package rs.sbnz.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import rs.sbnz.model.Permission;
import rs.sbnz.model.RbacRequest;
import rs.sbnz.model.Role;
import rs.sbnz.model.User;

class ServiceApplicationTests {
    @Test
    void testRbacRequest() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer(); 
        KieSession ksession = kContainer.newKieSession("ksessionPseudoClock");

        // --------------------------------------------------------------------
        // Create permissions and roles.
        // --------------------------------------------------------------------

        Permission p1 = new Permission("comment_on_articles");
        Permission p2 = new Permission("buy_article");
        Permission p3 = new Permission("sell_article");
        Permission p4 = new Permission("ban_user");
        Permission p5 = new Permission("unlock_system");

        Role rBasicClient = new Role("Basic client", "");
        Role rClient = new Role("Client", "");
        Role rAdmin = new Role("Admin", "");
        Role rSuperAdmin = new Role("Super Admin", "");

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

        // ksession.insert(u1);
        // ksession.insert(u2);
        // ksession.insert(u3);
        // ksession.insert(u4);
        // ksession.fireAllRules();

        // --------------------------------------------------------------------
        // Tests.
        // --------------------------------------------------------------------

        RbacRequest req = new RbacRequest(u1, p1);
        RbacRequest req2 = new RbacRequest(u1, p2);
        RbacRequest req3 = new RbacRequest(u1, p3);
        RbacRequest req4 = new RbacRequest(u2, p3);
        RbacRequest req5 = new RbacRequest(u2, p1);
        RbacRequest req6 = new RbacRequest(u2, p5);
        ksession.insert(req);
        ksession.insert(req2);
        ksession.insert(req3);
        ksession.insert(req4);
        ksession.insert(req5);
        ksession.insert(req6);

        ksession.fireAllRules();
        assertFalse(req.getRejected());
        assertFalse(req2.getRejected());
        assertTrue(req3.getRejected());
        assertFalse(req4.getRejected());
        assertFalse(req5.getRejected());
        assertTrue(req6.getRejected());
    }
}
