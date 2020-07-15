package bsa.java.concurrency.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class SearchResponseDTO {
    private UUID id;
    private String image;
    private double match;
}
