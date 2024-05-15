package rs.sbnz.service.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.sbnz.model.Role;

@Component
public class RoleService {
    @Autowired private IRoleRepo roleRepo;

    public Role save(Role role) {
        roleRepo.save(role);
        return role;
    }
}
