package ru.veretennikov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veretennikov.domain.Game;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {

//    https://vladmihalcea.com/hibernate-multiplebagfetchexception/     against cartesian product

    @Query("SELECT g " +
            "FROM Game g " +
            "LEFT JOIN FETCH g.genres " +
            "WHERE g.id = :id")
    Optional<Game> findOneWithGenresById(@Param("id") UUID id);

    @Query("SELECT g " +
            "FROM Game g " +
            "LEFT JOIN FETCH g.screens " +
            "WHERE g = :game")
    Optional<Game> findOneWithAllDetailsByGameWithGenres(@Param("game") Game game);

    @Query("SELECT g " +
            "FROM Game g " +
            "LEFT JOIN FETCH g.genres")
    List<Game> findAllWithGenres();

    @Query("SELECT g " +
            "FROM Game g " +
            "LEFT JOIN FETCH g.screens " +
            "WHERE g in :games")
    List<Game> findAllWithAllDetailsByGamesWithGenres(@Param("games") List<Game> games);

    @Query(value = "SELECT g " +
            "FROM Game g " +
            "WHERE lower(g.name) like lower(concat('%', :name, '%'))")
    List<Game> findAllByNameLike(@Param("name") String name);

}
