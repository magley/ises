package rs.sbnz.service.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.sbnz.model.Permission;
import rs.sbnz.model.Role;
import rs.sbnz.model.User;
import rs.sbnz.model.UserRole;
import rs.sbnz.model.article.Article;
import rs.sbnz.model.article.ArticleComment;
import rs.sbnz.model.article.ArticlePurchase;
import rs.sbnz.service.article.ArticleService;
import rs.sbnz.service.permission.PermissionService;
import rs.sbnz.service.role.RoleService;
import rs.sbnz.service.user.UserService;

@Component
public class DevServerInitializer {
    @Autowired private KieSession ksession;
    @Autowired private PermissionService permissionService;
    @Autowired private RoleService roleService;
    @Autowired private UserService userService;
    @Autowired private ArticleService articleService;

    public void initData() {
        Permission p01 = new Permission("comment_on_articles");
        Permission p02 = new Permission("buy_articles");
        Permission p03 = new Permission("sell_articles");

        Permission p04 = new Permission("view_reports");
        Permission p05 = new Permission("unblock_system");

        Role r01 = new Role("User", "Regular user.");
        Role r02 = new Role("Full user", "User will maximum priveleges");
        Role r03 = new Role("Admin", "Administrator of the system");
        Role r04 = new Role("SuperAdmin", "Admin will maximum priveleges");

        r01.getPermissions().add(p01);
        r01.getPermissions().add(p02);

        r02.setParent(r01);
        r02.getPermissions().add(p03);

        r03.getPermissions().add(p04);

        r04.setParent(r03);
        r04.getPermissions().add(p05);

        p01 = permissionService.save(p01);
        p02 = permissionService.save(p02);
        p03 = permissionService.save(p03);
        p04 = permissionService.save(p04);
        p05 = permissionService.save(p05);
        ksession.insert(p01);
        ksession.insert(p02);
        ksession.insert(p03);
        ksession.insert(p04);
        ksession.insert(p05);

        r01 = roleService.save(r01);
        r02 = roleService.save(r02);
        r03 = roleService.save(r03);
        r04 = roleService.save(r04);
        ksession.insert(r01);
        ksession.insert(r02);
        ksession.insert(r03);
        ksession.insert(r04);

        ksession.fireAllRules();

        ////////////////////////////////////////////////////////////////////////

        // password = admin123
        User u01 = new User(1L, "admin@sbnz.com", "$2a$10$85M6hDvkRHC5.vPon8tn2.dSD8vksJpRCr6N7BDRrwepOdhs2axqq", UserRole.ADMIN, "Adam", "Adamov");
        u01.setRbacRole(r04);
        u01 = userService.save(u01);

        // password = john123
        User u02 = new User(2L, "john@gmail.com", "$2a$10$6rcDsHTnQkoCGzI/As4PlOHbmoeWF5ebLyNA7qFKpW2RSQSqtt8aO", UserRole.CLIENT, "John", "Test");
        u02.setRbacRole(r01);
        u02 = userService.save(u02);

        // password = john123
        User u03 = new User(3L, "john2@gmail.com", "$2a$10$6rcDsHTnQkoCGzI/As4PlOHbmoeWF5ebLyNA7qFKpW2RSQSqtt8aO", UserRole.CLIENT, "John", "Bravo");
        u03.setRbacRole(r02);
        u03 = userService.save(u03);

        ////////////////////////////////////////////////////////////////////////

        Article a01 = new Article(1L, Instant.now().minus(10L, ChronoUnit.DAYS), u02, "Shirt", 123.0);
        a01 = articleService.save(a01);
        articleService.save(new ArticlePurchase(1L, u03, a01, Instant.now()));
        articleService.save(new ArticleComment(1L, Instant.now(), "Mine!", u02, a01));
        articleService.save(new ArticleComment(2L, Instant.now(), "Good...", u03, a01));

        Article a02 = new Article(2L, Instant.now(), u03, "Lamp", 456.0);
        a02 = articleService.save(a02);
        articleService.save(new ArticleComment(3L, Instant.now(), "too bright..........", u02, a02));
    }
}
