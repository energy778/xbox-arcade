package ru.veretennikov.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.dto.GameWithDetailsDTO;
import ru.veretennikov.service.GameService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/games")
public class MainController {

    private final GameService gameService;

    public MainController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value = "/full/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<List<GameWithDetailsDTO>> getAllGamesWithDetails() {
        List<GameWithDetailsDTO> games = gameService.getAllWithDetails();
        return games.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(games);
    }

    @GetMapping(value = "/full/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GameWithDetailsDTO> getGameWithDetails(@PathVariable("id") @NotNull UUID id) {
        return gameService.getByIdWithDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<List<GameDTO>> getAllGames() {
        List<GameDTO> games = gameService.getAll();
        return games.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(games);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GameDTO> getGame(@PathVariable("id") @NotNull UUID id) {
        return gameService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

}
