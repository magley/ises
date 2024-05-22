package rs.sbnz.service.permission;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.sbnz.model.Permission;

@Component
public class PermissionService {
    @Autowired private IPermissionRepo permissionRepo;

    public Optional<Permission> findByCode(String code) {
        return permissionRepo.findByCode(code);
    }

    public Permission save(Permission permission) {
        permissionRepo.save(permission);
        return permission;
    }
}
