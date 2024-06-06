package rs.sbnz.service.util;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.sbnz.model.Permission;
import rs.sbnz.model.RbacRequest;
import rs.sbnz.model.User;
import rs.sbnz.service.exceptions.UnauthorizedException;
import rs.sbnz.service.permission.PermissionService;

@Component
public class RBACUtil {
    @Autowired private AuthenticationFacade authFacade;
    @Autowired private PermissionService permissionService;
    @Autowired private KieSession kSession;

    public void preAuthorize2(String permissionCode) throws UnauthorizedException {
        User user = authFacade.getUser();
        Permission permission = permissionService.findByCode(permissionCode).orElseThrow(
            () -> new UnauthorizedException()
        );

        if (user.getRbacRole() == null) {
            throw new UnauthorizedException();
        }

        RbacRequest request = new RbacRequest(user, permission);
        kSession.insert(request);
        kSession.fireAllRules();

        if (request.getRejected()) {
            throw new UnauthorizedException();
        }
    }
}
