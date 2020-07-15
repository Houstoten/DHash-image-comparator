package bsa.java.concurrency.fs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class FileSystemService implements FileSystem {

    @Value("${path.imageFolder}")
    private String imageFolderPath;

    @Override
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
                throw new RuntimeException();
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteFile(UUID fileName) {
        return null;//todo delete file
    }
}
