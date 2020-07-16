package bsa.java.concurrency.fs;

import bsa.java.concurrency.exception.ImageBrokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class FileSystemService implements FileSystem {

    @Value("${path.imageFolder}")
    private String imageFolderPath;

    @Qualifier("asyncForMethods")
    @Autowired
    private Executor executor;

    @Override
    @Async("asyncForMethods")
    public CompletableFuture<Path> saveFile(byte[] incomeFile) throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
            File dir = new File(imageFolderPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(imageFolderPath + "/" + UUID.randomUUID() + ".jpg");
            try (OutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(incomeFile);
                return file.toPath();
            } catch (IOException e) {
                throw new ImageBrokenException();
            }
        },executor);
    }

    @Override
    @Async("asyncForMethods")
    public CompletableFuture<Void> deleteFile(UUID fileName) {
        return CompletableFuture.supplyAsync(() -> {
            new File(imageFolderPath + "/" + fileName + ".jpg")
                    .delete();
            return null;
        }, executor);
    }

    @Override
    @Async("asyncForMethods")
    public CompletableFuture<Void> purge() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.walk(Path.of(imageFolderPath))
                        .map(Path::toFile)
                        .forEach(File::delete);
                new File(imageFolderPath).delete();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, executor);
    }
}
