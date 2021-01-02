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
@Table(schema = Meta.schema, name = Meta.game_screen.name)
public class GameScreen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Meta.game_screen.fields.id)
    private Integer id;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = Meta.game_screen.fields.id_game, nullable = false)
    private Game game;

    @Column(name = Meta.game_screen.fields.name, nullable = false)
    private String name;

    @Column(name = Meta.game_screen.fields.url, nullable = false)
    private String url;

}
