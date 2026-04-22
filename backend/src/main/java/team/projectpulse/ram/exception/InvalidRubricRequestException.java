package team.projectpulse.ram.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRubricRequestException extends RuntimeException {

    public InvalidRubricRequestException(String message) {
        super(message);
    }
}
