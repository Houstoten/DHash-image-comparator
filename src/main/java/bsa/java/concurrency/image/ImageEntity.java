package bsa.java.concurrency.image;


import bsa.java.concurrency.exception.ImageBrokenException;
import bsa.java.concurrency.fs.FileSystem;
import bsa.java.concurrency.hasher.Hasher;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@NoArgsConstructor
@Entity
public class ImageEntity {

    @Id
    private UUID id;

    @Basic
    private Long hash;

    @Basic
    private String path;

    public ImageEntity(FileSystem fileSystemService, Hasher hasher, byte[] image) {
        try {
            fileSystemService.saveFile(image).thenCombine(hasher.diagonalHash(image), (sourcePath, hashGot) -> {
                hash = hashGot;
                id = UUID.fromString(sourcePath.getFileName().toString().split("\\.")[0]);
                path = sourcePath.toString();
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ImageBrokenException();
        }
    }
}
