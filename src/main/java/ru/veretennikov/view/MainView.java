package ru.veretennikov.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.service.GameService;

@Route
public class MainView extends VerticalLayout {

    private final GameService gameService;
    final Grid<GameDTO> grid;

    public MainView(GameService gameService) {
        this.gameService = gameService;
        this.grid = new Grid<>(GameDTO.class);
        add(grid);
        listGames();
    }

    void listGames() {
        grid.setItems(gameService.getAll());
    }

}
