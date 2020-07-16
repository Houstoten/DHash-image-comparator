package bsa.java.concurrency.image;

import bsa.java.concurrency.exception.ImageBrokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/image")
@ControllerAdvice
@Slf4j
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> uploadSizeExceed(MaxUploadSizeExceededException e) {
        log.error("Uploaded file size exceed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("Whoops, here is an error", "Uploaded file size must be less than " + maxSize));
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void batchUploadImages(@RequestParam("images") MultipartFile[] files) {
        log.info("Batch upload requested");
        imageService.batchUploadImages(files);
        log.info("Batch upload done");
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> searchMatches(@RequestParam("image") MultipartFile file
            , @RequestParam(value = "threshold", defaultValue = "0.9") double threshold) {
        try {
            log.info("Match search requested");
            var response = imageService.searchMatches(file, threshold);
            if (response.isEmpty()) {
                log.info("No matches found for request");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("Whoops, here is an error", "Cannot found any image similar to yours."));
            }
            log.info("Match search request done");
            return ResponseEntity.ok(response);
        } catch (IOException | ImageBrokenException e) {
            log.error("Match search error:  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Whoops, here is an error", e.getMessage()));
        } catch (InputMismatchException e) {
            log.error("Threshold error :  " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Whoops, here is an error", "threshold " + threshold + " out of bounds"));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable("id") UUID imageId) {
        log.info("Delete image with id " + imageId + " requested");
        imageService.deleteImage(imageId);
        log.info("Delete image with id " + imageId + " request done");
    }

    @DeleteMapping("/purge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purgeImages() {
        log.info("Purge images requested");
        imageService.purgeImages();
        log.info("Purge images request done");
    }
}
