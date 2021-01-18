package ru.veretennikov.service;

import org.springframework.stereotype.Service;
import ru.veretennikov.domain.FavouriteGame;
import ru.veretennikov.repository.FavouriteGameRepository;
import ru.veretennikov.repository.GameRepository;

import java.util.UUID;

@Service
public class FavouriteGameServiceImpl implements FavouriteGameService {

    private final FavouriteGameRepository repository;
    private final GameRepository gameRepository;

    public FavouriteGameServiceImpl(FavouriteGameRepository repository, GameRepository gameRepository) {
        this.repository = repository;
        this.gameRepository = gameRepository;
    }

    public void add(UUID idGame) {
        gameRepository.findById(idGame).ifPresent(game ->
                repository.findByGame(game).orElseGet(() ->
                        repository.save(FavouriteGame.builder().game(game).build())));
    }

    public void delete(UUID idGame) {
        gameRepository.findById(idGame)
                .flatMap(repository::findByGame)
                .ifPresent(repository::delete);
    }

}
