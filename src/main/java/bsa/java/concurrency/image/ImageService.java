package bsa.java.concurrency.image;

import bsa.java.concurrency.exception.ImageBrokenException;
import bsa.java.concurrency.fs.FileSystem;
import bsa.java.concurrency.hasher.Hasher;
import bsa.java.concurrency.image.dto.SearchResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ImageService {

    @Autowired
    private Environment environment;

    @Autowired
    private FileSystem fileSystemService;

    @Autowired
    private Hasher hashCalculator;

    @Autowired
    private ImageRepository imageRepository;

    @Value("${path.imageFolder}")
    private String cacheFolder;

    @Qualifier("asyncForMethods")
    @Autowired
    private Executor executor;

    public void batchUploadImages(MultipartFile[] uploadImages) {
        Stream.of(uploadImages)
                .parallel()
                .map(this::createFileChain)
                .map(CompletableFuture::join)
                .forEach(imageRepository::save);
    }

    @Async("asyncForMethods")
    CompletableFuture<ImageEntity> createFileChain(MultipartFile file) {
        try {
            log.info("async started in createFileChain by thread " + Thread.currentThread().getId());
            var bytes = file.getBytes();
            return CompletableFuture.supplyAsync(() -> new ImageEntity(fileSystemService
                    , hashCalculator, bytes));
        } catch (IOException e) {
            throw new ImageBrokenException();
        }
    }

    public List<SearchResponseDTO> searchMatches(MultipartFile file, double threshold) throws ImageBrokenException
            , IOException, InputMismatchException {
        if(threshold<0||threshold>1){
            throw new InputMismatchException(threshold+" out of bounds");
        }
        var image = file.getBytes();
        var hashReceived = hashCalculator.diagonalHash(image).join();
        var list = imageRepository.findPathByHash(hashReceived, threshold).join();
        if (list.isEmpty()) {
            executor.execute(() -> imageRepository.save(ImageEntity.fullfill(new ImageEntity()
                    , fileSystemService.saveFile(image).join(), hashReceived)));
        }
        return list.stream()
                .map(result -> new SearchResponseDTO(result.getImageId()
                        , InetAddress.getLoopbackAddress().getHostName()
                        + ":"
                        + environment.getProperty("server.port")
                        + "/" + cacheFolder + "/"
                        + Path.of(result.getImageUrl()).getFileName()
                        , result.getMatchPercent()))
                .collect(Collectors.toList());
    }

    public void deleteImage(UUID id) {
        CompletableFuture.allOf(CompletableFuture.runAsync(() -> imageRepository.deleteById(id))
                , fileSystemService.deleteFile(id)).join();
    }

    public void purgeImages() {
        CompletableFuture.allOf(CompletableFuture.runAsync(() -> imageRepository.deleteAll())
                , fileSystemService.purge()).join();
    }
}
