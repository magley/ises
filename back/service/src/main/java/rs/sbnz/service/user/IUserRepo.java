package rs.sbnz.service.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.sbnz.model.User;

public interface IUserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
