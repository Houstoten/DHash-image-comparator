package bsa.java.concurrency.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Your image is broken. Try another one :^)")
public class ImageBrokenException extends RuntimeException {
    public ImageBrokenException(){
        super("Whoops! Broken image :(");
    }
}
