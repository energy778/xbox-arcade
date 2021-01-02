package ru.veretennikov.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.veretennikov.domain.Game;
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

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> getAllGames() {
        List<Game> games = gameService.getAll();
        return ResponseEntity.ok(games);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getGame(@PathVariable("id") @NotNull UUID uuid) {
        Game game = gameService.getById(uuid);
        return ResponseEntity.ok(game);
    }

}
