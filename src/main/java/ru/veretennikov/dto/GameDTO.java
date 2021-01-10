package ru.veretennikov.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@FieldNameConstants(asEnum = true)
public class GameDTO {

    private UUID id;

    private String name;

    private String gameUrl;

    private String picUrl;

    private LocalDate releaseDate;

    private String description1;

    private String description2;

    private String rating;

    private Integer price;

    private String location;

    private boolean availability;

    private LocalDate dateIssue;

    private String developer;

    private String publisher;

//    use Optional in DTO and model
//    https://stackoverflow.com/a/26328314/5406779
//    https://dev.to/piczmar_0/java-optional-in-class-fields-why-not-40df

}
