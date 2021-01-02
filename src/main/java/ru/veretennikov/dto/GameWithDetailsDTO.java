package ru.veretennikov.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
public class GameWithDetailsDTO {

    private UUID id;

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
    
    private List<GameGenreDTO> genres;
    
    private List<GameScreenDTO> screens;

    @Builder
    @Getter
    @Setter
    public static class GameGenreDTO {
        private String name;
    }

    @Builder
    @Getter
    @Setter
    public static class GameScreenDTO {
        private String name;
        private String url;
    }

}
