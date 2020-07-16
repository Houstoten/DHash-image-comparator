package bsa.java.concurrency.image;

import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ImageRepository extends JpaRepository<ImageEntity, UUID> {


    //    @Query(value = "SELECT path as imageUrl, cast(id as varchar) as imageId, " +
//            " (1 - LENGTH( REPLACE( CAST ( cast((hash # :toSearch) as bit(64)) " +
//            " AS TEXT ), '0', ''))\\:\\:decimal / 64) as matchPercent " +
//            " FROM image_entity " +
//            " where (1 - LENGTH( REPLACE( CAST ( cast((hash # :toSearch) as bit(64)) " +
//            " AS TEXT ), '0', ''))\\:\\:decimal / 64) >= :threshold", nativeQuery = true)//Query with not migrated function
    @Async
    @Query(value = "SELECT path as imageUrl, cast(id as varchar) as imageId, " +
            "match_percent(hash, :toSearch) as matchPercent FROM image_entity " +
            "where match_percent(hash, :toSearch) >= :threshold", nativeQuery = true)
    CompletableFuture<List<SearchResultDTO>> findPathByHash(long toSearch, double threshold);
}
