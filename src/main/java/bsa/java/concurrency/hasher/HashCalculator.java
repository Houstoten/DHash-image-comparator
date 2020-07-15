package bsa.java.concurrency.hasher;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class HashCalculator implements Hasher{
    public CompletableFuture<Long> diagonalHash(byte[] image){
        return CompletableFuture.completedFuture(0L);
    }
}
