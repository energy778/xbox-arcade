package ru.veretennikov.service;

import ru.veretennikov.domain.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewService {
    List<Review> findAllByGameId(UUID idCurrentGame);
    Optional<Review> getById(Integer id);
    void save(Review currentReview);
    void delete(Integer id);
}
