package team.projectpulse.ram.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStudentRequestException extends RuntimeException {

    public InvalidStudentRequestException(String message) {
        super(message);
    }
}
