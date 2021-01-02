package ru.veretennikov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veretennikov.domain.Game;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {

    @Query("SELECT g FROM Game g " +
            "LEFT JOIN FETCH g.genres gg " +
            "WHERE g.id = :id")
    Game findOneWithGenresById(@Param("id") UUID id);

    @Query("SELECT g FROM Game g " +
            "LEFT JOIN FETCH g.screens gs " +
            "WHERE g = :game")
    Game findOneWithAllDetailsByGame(@Param("game") Game game);

    @Query("SELECT g FROM Game g " +
            "LEFT JOIN FETCH g.genres gg")
    List<Game> findAllWithGenres();

    @Query("SELECT g FROM Game g " +
            "LEFT JOIN FETCH g.screens gs " +
            "WHERE g in :games")
    List<Game> findAllWithAllDetailsByGamesWithGenres(@Param("games") List<Game> games);

}
