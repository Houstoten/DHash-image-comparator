package bsa.java.concurrency.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Your image is broken. Try another one :^)")
@Slf4j
public class ImageBrokenException extends RuntimeException {
    public ImageBrokenException() {
        super("Whoops! Broken image :(");
        log.error("Whoops! Broken image :(");
    }
}
