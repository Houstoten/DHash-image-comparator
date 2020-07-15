package bsa.java.concurrency.hasher;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class HashCalculator implements Hasher {
    public CompletableFuture<Long> diagonalHash(byte[] image) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Image img = ImageIO.read(new ByteArrayInputStream(image))
                        .getScaledInstance(9, 9, Image.SCALE_SMOOTH);
                BufferedImage imgGray = new BufferedImage(img.getWidth(null)
                        , img.getHeight(null)
                        , BufferedImage.TYPE_BYTE_GRAY);
                Graphics g = imgGray.getGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                long hash = 0;
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (new Color(imgGray.getRGB(i + 1, j + 1)).getRed()
                                > new Color(imgGray.getRGB(i, j)).getRed()) {
                            hash |= 1;
                        }
                        hash<<= 1;
                    }
                }
                return hash;
            } catch (IOException e) {
                throw new RuntimeException();
            }
        });
    }
}
