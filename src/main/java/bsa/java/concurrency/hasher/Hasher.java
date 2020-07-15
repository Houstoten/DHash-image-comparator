package bsa.java.concurrency.hasher;

import java.util.concurrent.CompletableFuture;

public interface Hasher {
    CompletableFuture<Long> diagonalHash(byte[] image);
}
