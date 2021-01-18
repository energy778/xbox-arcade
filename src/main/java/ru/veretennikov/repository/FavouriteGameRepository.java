package ru.veretennikov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veretennikov.domain.FavouriteGame;
import ru.veretennikov.domain.Game;

import java.util.Optional;

@Repository
public interface FavouriteGameRepository extends JpaRepository<FavouriteGame, Integer> {

    @Query("select fg " +
            "from FavouriteGame fg " +
            "where fg.game = :game")
    Optional<FavouriteGame> findByGame(@Param("game") Game game);

}
