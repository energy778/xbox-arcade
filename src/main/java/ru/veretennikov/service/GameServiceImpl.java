package ru.veretennikov.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veretennikov.domain.Game;
import ru.veretennikov.domain.GameGenre;
import ru.veretennikov.domain.GameScreen;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.dto.GameWithDetailsDTO;
import ru.veretennikov.repository.GameRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository repository;

    public GameServiceImpl(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<GameDTO> getAll() {
        return repository.findAll().stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GameDTO> getById(UUID uuid) {
        return repository.findById(uuid).map(this::buildDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameWithDetailsDTO> getAllWithDetails() {
        List<Game> games = repository.findAllWithGenres();
        return repository.findAllWithAllDetailsByGamesWithGenres(games).stream()
                .map(this::buildDTOWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GameWithDetailsDTO> getByIdWithDetails(UUID uuid) {
        return repository.findOneWithAllDetailsByGameWithGenres(
                repository.findOneWithGenresById(uuid).orElse(null))
                .map(this::buildDTOWithDetails);
    }


    private GameDTO buildDTO(Game game) {
        return GameDTO.builder()
                .id(game.getId())
                .gameUrl(game.getGameUrl())
                .picUrl(game.getPicUrl())
                .releaseDate(game.getReleaseDate())
                .description1(game.getDescription1())
                .description2(game.getDescription2())
                .rating(game.getRating())
                .price(game.getPrice())
                .location(game.getLocation())
                .availability(game.isAvailability())
                .dateIssue(game.getDateIssue())
                .developer(game.getDeveloper())
                .publisher(game.getPublisher())
                .build();
    }

    private GameWithDetailsDTO buildDTOWithDetails(Game game) {
        return GameWithDetailsDTO.builder()
                .id(game.getId())
                .gameUrl(game.getGameUrl())
                .picUrl(game.getPicUrl())
                .releaseDate(game.getReleaseDate())
                .description1(game.getDescription1())
                .description2(game.getDescription2())
                .rating(game.getRating())
                .price(game.getPrice())
                .location(game.getLocation())
                .availability(game.isAvailability())
                .dateIssue(game.getDateIssue())
                .developer(game.getDeveloper())
                .publisher(game.getPublisher())
                .genres(game.getGenres().stream()
                        .map(this::buildGenreDTO)
                        .collect(Collectors.toList()))
                .screens(game.getScreens().stream()
                        .map(this::buildScreenDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private GameWithDetailsDTO.GameScreenDTO buildScreenDTO(GameScreen gameScreen) {
        return GameWithDetailsDTO.GameScreenDTO.builder()
                .name(gameScreen.getName())
                .url(gameScreen.getUrl())
                .build();
    }

    private GameWithDetailsDTO.GameGenreDTO buildGenreDTO(GameGenre gameGenre) {
        return GameWithDetailsDTO.GameGenreDTO.builder()
                .name(gameGenre.getName())
                .build();
    }

}
