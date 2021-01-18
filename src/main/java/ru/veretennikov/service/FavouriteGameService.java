package ru.veretennikov.service;

import java.util.UUID;

public interface FavouriteGameService {
    void add(UUID idGame);
    void delete(UUID idGame);
}
