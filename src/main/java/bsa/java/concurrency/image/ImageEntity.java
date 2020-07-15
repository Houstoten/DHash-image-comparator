package bsa.java.concurrency.image;


import bsa.java.concurrency.fs.FileSystem;
import bsa.java.concurrency.hasher.Hasher;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@NoArgsConstructor
@Entity
public class ImageEntity {

    @Id
    private UUID id;

    @Basic
    private Long hash;

    @Basic
    private String path;

    public ImageEntity(ExecutorService executorService, FileSystem fileSystemService, Hasher hasher, byte[] image) {
        try {
            var sourcePath = fileSystemService.saveFile(image).join();
            hash = hasher.diagonalHash(image).join();
            id = UUID.fromString(sourcePath.getFileName().toString().split("\\.")[0]);
            path = sourcePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
