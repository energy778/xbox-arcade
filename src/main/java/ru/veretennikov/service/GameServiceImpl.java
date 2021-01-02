package ru.veretennikov.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veretennikov.domain.Game;
import ru.veretennikov.repository.GameRepository;

import java.util.List;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository repository;

    public GameServiceImpl(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Game> getAll() {
        List<Game> games = repository.findAllWithGenres();
        return repository.findAllWithAllDetailsByGamesWithGenres(games);
    }

    @Override
    @Transactional(readOnly = true)
    public Game getById(UUID uuid) {
        Game game = repository.findOneWithGenresById(uuid);
        return repository.findOneWithAllDetailsByGameWithGenres(game);
    }

}
