package ru.veretennikov.domain;

import lombok.*;
import ru.veretennikov.domain.meta.Meta;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = Meta.schema, name = Meta.game.name)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Meta.game.fields.id)
    private UUID id;

    @Column(name = Meta.game.fields.game_url)
    private String game_url;

    @Column(name = Meta.game.fields.pic_url)
    private String pic_url;

    @Column(name = Meta.game.fields.release_date)
    private LocalDate release_date;

    @Column(name = Meta.game.fields.description1)
    private String description1;

    @Column(name = Meta.game.fields.description2)
    private String description2;

    @Column(name = Meta.game.fields.rating)
    private String rating;

    @Column(name = Meta.game.fields.price)
    private Integer price;

    @Column(name = Meta.game.fields.location)
    private String location;

    @Column(name = Meta.game.fields.availability, nullable = false)
    private boolean availability;

    @Column(name = Meta.game.fields.date_issue)
    private LocalDate date_issue;

    @Column(name = Meta.game.fields.developer)
    private String developer;

    @Column(name = Meta.game.fields.publisher)
    private String publisher;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = Meta.game_genre.fields.game, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameGenre> genres;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = Meta.game_screen.fields.game, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameScreen> screens;

}
