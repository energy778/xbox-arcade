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
@Table(schema = Meta.schema, name = Meta.favourite.name)
public class FavouriteGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Meta.favourite.fields.id)
    private Integer id;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = Meta.favourite.fields.id_game, nullable = false)
    private Game game;

}
