package bsa.java.concurrency.image;


import bsa.java.concurrency.exception.ImageBrokenException;
import bsa.java.concurrency.fs.FileSystem;
import bsa.java.concurrency.hasher.Hasher;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Slf4j
public class ImageEntity {

    @Id
    private UUID id;

    @Basic
    private Long hash;

    @Basic
    private String path;

    public ImageEntity(FileSystem fileSystemService, Hasher hasher, byte[] image) {
        try {
            fileSystemService.saveFile(image).thenCombine(hasher.diagonalHash(image)
                    , (sourcePath, hashGot) -> fullfill(this, sourcePath, hashGot)).get();
            log.info("Image entity with id " + id + " created");
        } catch (InterruptedException | ExecutionException e) {
            throw new ImageBrokenException();
        }
    }

    public static ImageEntity fullfill(ImageEntity entity, Path sourcePath, long hashGot) {
        entity.hash = hashGot;
        entity.id = UUID.fromString(sourcePath.getFileName().toString().split("\\.")[0]);
        entity.path = sourcePath.toString();
        return entity;
    }
}
