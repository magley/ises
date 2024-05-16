package rs.sbnz.service.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.sbnz.model.Request;

public interface IRequestRepo extends JpaRepository<Request, Long> {
    
}
