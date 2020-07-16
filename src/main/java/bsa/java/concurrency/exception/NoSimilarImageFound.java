package bsa.java.concurrency.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Cannot find anything similar :(")
public class NoSimilarImageFound extends RuntimeException {
    public NoSimilarImageFound(String message) {
        super(message);
    }
}
