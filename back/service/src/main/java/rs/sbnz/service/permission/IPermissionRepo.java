package rs.sbnz.service.permission;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.sbnz.model.Permission;

public interface IPermissionRepo extends JpaRepository<Permission, Long> {
    public Optional<Permission> findByCode(String code);
}
