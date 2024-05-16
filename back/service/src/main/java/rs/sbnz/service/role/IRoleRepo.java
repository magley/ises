package rs.sbnz.service.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.sbnz.model.Role;

public interface IRoleRepo extends JpaRepository<Role, Long> {
    
}
