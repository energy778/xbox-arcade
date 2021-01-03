package ru.veretennikov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.veretennikov.domain.Game;
import ru.veretennikov.domain.GameGenre;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameGenreRepository extends JpaRepository<GameGenre, UUID> {

    @Query("select gg from GameGenre gg where gg.game.id = :id")
    List<GameGenre> findAllByGameId(UUID id);

}
