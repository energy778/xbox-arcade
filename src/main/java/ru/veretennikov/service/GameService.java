package ru.veretennikov.service;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.veretennikov.domain.Game;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.dto.GameWithDetailsDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameService {
    List<GameDTO> getAll();
    Optional<GameDTO> getById(UUID uuid);
    List<GameWithDetailsDTO> getAllWithDetails();
    Optional<GameWithDetailsDTO> getByIdWithDetails(UUID uuid);

    void delete(UUID id);
    GameWithDetailsDTO save(GameWithDetailsDTO currentGame);

    List<GameDTO> fetch(int offset, int limit, Sort sort);
    List<GameDTO> fetch(String like, int offset, int limit, Sort sort);
    List<GameDTO> fetch(Specification<Game> specification, int offset, int limit, Sort sort);
    long count();
    long count(String like);
    long count(Specification<Game> specification);
}
