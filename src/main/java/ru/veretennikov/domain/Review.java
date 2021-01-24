package ru.veretennikov.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.veretennikov.domain.meta.Meta;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = Meta.schema, name = Meta.review.name)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Meta.review.fields.id)
    private Integer id;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = Meta.review.fields.id_game, nullable = false)
    private Game game;

    @Column(name = Meta.review.fields.updated)
    private LocalDate dateUpdated;

    @Column(name = Meta.review.fields.note)
    private String note;

}
