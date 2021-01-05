package ru.veretennikov.service;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veretennikov.domain.Game;
import ru.veretennikov.domain.GameGenre;
import ru.veretennikov.domain.GameScreen;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.dto.GameWithDetailsDTO;
import ru.veretennikov.repository.GameGenreRepository;
import ru.veretennikov.repository.GameRepository;
import ru.veretennikov.repository.GameScreenRepository;
import ru.veretennikov.util.db.OffsetBasedPageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository repository;
    private final GameGenreRepository genreRepository;
    private final GameScreenRepository screenRepository;

    public GameServiceImpl(GameRepository repository, GameGenreRepository genreRepository, GameScreenRepository screenRepository) {
        this.repository = repository;
        this.genreRepository = genreRepository;
        this.screenRepository = screenRepository;
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

    @Override
    public List<GameDTO> getAllByNameLike(String name) {
        return repository.findAllByNameLike(name).stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public GameWithDetailsDTO save(GameWithDetailsDTO gameWithDetailsDTO) {
        Game gameForSave = Game.builder()
                .id(gameWithDetailsDTO.getId())
                .name(gameWithDetailsDTO.getName())
                .gameUrl(gameWithDetailsDTO.getGameUrl())
                .picUrl(gameWithDetailsDTO.getPicUrl())
                .releaseDate(gameWithDetailsDTO.getReleaseDate())
                .description1(gameWithDetailsDTO.getDescription1())
                .description2(gameWithDetailsDTO.getDescription2())
                .rating(gameWithDetailsDTO.getRating())
                .price(gameWithDetailsDTO.getPrice())
                .location(gameWithDetailsDTO.getLocation())
                .availability(gameWithDetailsDTO.isAvailability())
                .dateIssue(gameWithDetailsDTO.getDateIssue())
                .developer(gameWithDetailsDTO.getDeveloper())
                .publisher(gameWithDetailsDTO.getPublisher())
                // TODO: 03.01.21 genres and screens may be editable
                .genres(Optional.ofNullable(gameWithDetailsDTO.getId())
                        .map(genreRepository::findAllByGameId)
                        .orElse(new ArrayList<>()))
                .screens(Optional.ofNullable(gameWithDetailsDTO.getId())
                        .map(screenRepository::findAllByGameId)
                        .orElse(new ArrayList<>()))
                .build();

        Game game = repository.save(gameForSave);

        return buildDTOWithDetails(game);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public long count(String like) {
        return repository.findAllByNameLike(like).size();
    }

    @Override
    public long count(Specification<Game> specification) {
        return repository.count(specification);
    }

    @Override
    public List<GameDTO> fetch(int offset, int limit) {
        return repository.findAll(new OffsetBasedPageRequest(offset, limit)).stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> fetch(String like, int offset, int limit) {
        return repository.findAllByNameLike(like, new OffsetBasedPageRequest(offset, limit)).stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> fetch(Specification<Game> specification, int offset, int limit) {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(offset, limit);
        Page<Game> page = repository.findAll(specification, pageRequest);
        return page.get()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }


    private GameDTO buildDTO(Game game) {
        return GameDTO.builder()
                .id(game.getId())
                .name(game.getName())
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
                .name(game.getName())
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
