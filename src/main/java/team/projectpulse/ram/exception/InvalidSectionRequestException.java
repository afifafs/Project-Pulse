package team.projectpulse.ram.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSectionRequestException extends RuntimeException {

    public InvalidSectionRequestException(String message) {
        super(message);
    }
}
