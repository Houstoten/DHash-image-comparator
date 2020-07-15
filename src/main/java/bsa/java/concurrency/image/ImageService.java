package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystem;
import bsa.java.concurrency.hasher.Hasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
public class ImageService {

    @Autowired
    private FileSystem fileSystemService;

    @Autowired
    private Hasher hashCalculator;

    @Autowired
    private ImageRepository imageRepository;

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void batchUploadImages(MultipartFile[] uploadImages) {
        Stream.of(uploadImages)
                .parallel()
                .map(this::createFileChain)
                .filter(Objects::nonNull)
                .map(CompletableFuture::join)
                .forEach(imageRepository::save);
    }

    private CompletableFuture<ImageEntity> createFileChain(MultipartFile file) {
        try {
            var bytes = file.getBytes();
            return CompletableFuture.supplyAsync(() -> new ImageEntity(executorService, fileSystemService, hashCalculator, bytes));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
