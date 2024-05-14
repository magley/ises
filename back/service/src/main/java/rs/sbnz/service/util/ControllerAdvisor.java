package rs.sbnz.service.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import rs.sbnz.service.exceptions.IPBlockedException;

@ControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler(IPBlockedException.class)
    public ResponseEntity<?> exception(IPBlockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
