package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystem;
import bsa.java.concurrency.hasher.Hasher;
import bsa.java.concurrency.image.dto.SearchResponseDTO;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ImageService {

    @Autowired
    private Environment environment;

    @Autowired
    private FileSystem fileSystemService;

    @Autowired
    private Hasher hashCalculator;

    @Autowired
    private ImageRepository imageRepository;

//    @Value("${server.servlet.contextPath}")
//    private String contextPath;

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void batchUploadImages(MultipartFile[] uploadImages) {
        Stream.of(uploadImages)
                .parallel()
                .map(this::createFileChain)
                .map(CompletableFuture::join)
                .forEach(imageRepository::save);
    }

    private CompletableFuture<ImageEntity> createFileChain(MultipartFile file) {
        try {
            var bytes = file.getBytes();
            return CompletableFuture.supplyAsync(() -> new ImageEntity(executorService, fileSystemService
                    , hashCalculator, bytes));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public List<SearchResponseDTO> searchMatches(MultipartFile file, double threshold) throws IOException {
        return imageRepository.findPathByHash(hashCalculator.diagonalHash(file.getBytes()).join(), threshold)
                .join()
                .stream()
                .map(result -> new SearchResponseDTO(result.getImageId()
                        , InetAddress.getLoopbackAddress().getHostName()
                        + ":"
                        + environment.getProperty("server.port")
                        + "/images/"
                        + Path.of(
                        "/" + result.getImageUrl()).getFileName()
                        , result.getMatchPercent()))
                .collect(Collectors.toList());
    }
}
