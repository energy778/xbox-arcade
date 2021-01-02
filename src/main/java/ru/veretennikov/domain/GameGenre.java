package ru.veretennikov.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.veretennikov.domain.meta.Meta;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = Meta.schema, name = Meta.game_genre.name)
public class GameGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Meta.game_genre.fields.id)
    private Integer id;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = Meta.game_genre.fields.id_game, nullable = false)
    private Game game;

    @Column(name = Meta.game_genre.fields.genre, nullable = false)
    private String genre;

}
