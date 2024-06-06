package rs.sbnz.service.role;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.sbnz.model.Role;

public interface IRoleRepo extends JpaRepository<Role, Long> {
    
}
