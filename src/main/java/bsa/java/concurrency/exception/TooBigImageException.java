package bsa.java.concurrency.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST
        , reason = "Whoops! Your image is too large for me. Please feed with image less than 10 Mb")
public class TooBigImageException extends RuntimeException {
}
