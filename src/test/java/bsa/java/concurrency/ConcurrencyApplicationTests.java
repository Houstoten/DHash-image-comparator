package bsa.java.concurrency;

import bsa.java.concurrency.image.ImageRepository;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ConcurrencyApplicationTests {

    @Autowired
    ImageRepository imageRepository;


    @Test
    void contextLoads() {
    }

    @Test
    void cc() {
        List<SearchResultDTO> binaries = imageRepository.findPathByHash(-4777783188396986484L, 1).join();
        assertEquals(binaries.size(), 2);
    }

}
