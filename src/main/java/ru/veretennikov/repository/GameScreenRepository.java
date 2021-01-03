package ru.veretennikov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.veretennikov.domain.GameGenre;
import ru.veretennikov.domain.GameScreen;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameScreenRepository extends JpaRepository<GameScreen, UUID> {

    @Query("select gs from GameScreen gs where gs.game.id = :id")
    List<GameScreen> findAllByGameId(UUID id);

}
