package rs.sbnz.service.request;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.sbnz.model.Request;

public interface IRequestRepo extends JpaRepository<Request, Long> {
    
}
