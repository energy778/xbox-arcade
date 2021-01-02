package ru.veretennikov.service;

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

    List<GameDTO> getAllByNameLike(String filterText);
}
