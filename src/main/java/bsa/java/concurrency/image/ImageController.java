package bsa.java.concurrency.image;

import bsa.java.concurrency.exception.ImageBrokenException;
import bsa.java.concurrency.exception.NoSimilarImageFound;
import bsa.java.concurrency.image.dto.SearchResponseDTO;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/image")
@ControllerAdvice
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> uploadSizeExceed(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("Whoops, here is an error", "Uploaded file size must be less than " + maxSize));
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void batchUploadImages(@RequestParam("images") MultipartFile[] files) {
        imageService.batchUploadImages(files);
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> searchMatches(@RequestParam("image") MultipartFile file
            , @RequestParam(value = "threshold", defaultValue = "0.9") double threshold) {
        try {
            var response = imageService.searchMatches(file, threshold);
            if (response.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("Whoops, here is an error", "Cannot found any image similar to yours."));
            }
            return ResponseEntity.ok(response);
        } catch (IOException | ImageBrokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Whoops, here is an error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable("id") UUID imageId) {
        imageService.deleteImage(imageId);
    }

    @DeleteMapping("/purge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purgeImages() {
        imageService.purgeImages();
    }
}
