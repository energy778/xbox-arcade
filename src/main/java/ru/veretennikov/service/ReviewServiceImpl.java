package ru.veretennikov.service;

import org.springframework.stereotype.Service;
import ru.veretennikov.domain.Review;
import ru.veretennikov.repository.ReviewRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repository;
    private final GameService gameService;

    public ReviewServiceImpl(ReviewRepository repository, GameService gameService) {
        this.repository = repository;
        this.gameService = gameService;
    }

    @Override
    public Optional<Review> getById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public void save(Review currentReview) {
        repository.save(currentReview);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<Review> findAllByGameId(UUID idCurrentGame) {
        if (idCurrentGame == null)
            return Collections.emptyList();
        return repository.findAllByGame(gameService.getOne(idCurrentGame));
    }

}
