package ru.veretennikov.service;

import ru.veretennikov.domain.Game;

import java.util.List;
import java.util.UUID;

public interface GameService {
    List<Game> getAll();
    Game getById(UUID uuid);
}
