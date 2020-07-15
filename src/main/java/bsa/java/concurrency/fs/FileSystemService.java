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
    public CompletableFuture<Path> saveFile(byte[] incomeFile) throws IOException {
        File dir = new File(imageFolderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(imageFolderPath + "/" + UUID.randomUUID() + ".jpg");
        try (OutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(incomeFile);
            return CompletableFuture.completedFuture(file.toPath());
        }
    }

    @Override
    public CompletableFuture<Void> deleteFile(UUID fileName) {
        return null;//todo delete file
    }
}
