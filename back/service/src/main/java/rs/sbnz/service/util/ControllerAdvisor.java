package rs.sbnz.service.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import rs.sbnz.service.exceptions.IPBlockedException;
import rs.sbnz.service.exceptions.NotFoundException;
import rs.sbnz.service.exceptions.UnauthorizedException;

@ControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler(IPBlockedException.class)
    public ResponseEntity<?> exception_IPBlockedException(IPBlockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> exception_UnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> exception_NotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
