package bsa.java.concurrency.fs;

import bsa.java.concurrency.exception.ImageBrokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class FileSystemService implements FileSystem {

    @Value("${path.imageFolder}")
    private String imageFolderPath;

    @Override
    @Async("asyncForMethods")
    public CompletableFuture<Path> saveFile(byte[] incomeFile) throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
            log.info("async started in saver by thread " + Thread.currentThread().getId());
            File dir = new File(imageFolderPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(imageFolderPath + "/" + UUID.randomUUID() + ".jpg");
            try (OutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(incomeFile);
                log.info("async ended in saver by thread " + Thread.currentThread().getId());
                return file.toPath();
            } catch (IOException e) {
                throw new ImageBrokenException();
            }
        });
    }

    @Override
    @Async("asyncForMethods")
    public CompletableFuture<Void> deleteFile(UUID fileName) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("async started in file delete by thread " + Thread.currentThread().getId());
            new File(imageFolderPath + "/" + fileName + ".jpg")
                    .delete();
            log.info("async ended in file delete by thread " + Thread.currentThread().getId());
            return null;
        });
    }

    @Override
    @Async("asyncForMethods")
    public CompletableFuture<Void> purge() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("async started in folder delete by thread " + Thread.currentThread().getId());
                Files.walk(Path.of(imageFolderPath))
                        .map(Path::toFile)
                        .forEach(File::delete);
                new File(imageFolderPath).delete();
                log.info("async ended in folder delete by thread " + Thread.currentThread().getId());
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
