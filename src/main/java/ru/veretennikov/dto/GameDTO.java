package ru.veretennikov.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
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

}
