package ru.veretennikov.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@FieldNameConstants(asEnum = true)
public class GameDTO {

    public GameDTO(UUID id, String name, String picUrl, LocalDate releaseDate, String rating, Integer price, boolean availability, String developer, String publisher, boolean favorite) {
        this.id = id;
        this.name = name;
        this.picUrl = picUrl;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.price = price;
        this.availability = availability;
        this.developer = developer;
        this.publisher = publisher;
        this.favorite = favorite;
    }

    private UUID id;

    private String name;

    private String picUrl;

    private LocalDate releaseDate;

    private String rating;

    private Integer price;

    private boolean availability;

    private String developer;

    private String publisher;

    private boolean favorite;

//    use Optional in DTO and model
//    https://stackoverflow.com/a/26328314/5406779
//    https://dev.to/piczmar_0/java-optional-in-class-fields-why-not-40df

}
