package ru.veretennikov.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.service.GameService;

@Route
public class MainView extends VerticalLayout {

    private final GameService gameService;
    final Grid<GameDTO> grid;

    public MainView(GameService gameService) {
        this.gameService = gameService;
        this.grid = new Grid<>(GameDTO.class);

        TextField filter = new TextField();
        filter.setPlaceholder("Filter by last name");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listGames(e.getValue()));
        add(filter, grid);
    }

    void listGames(String filterText) {
        if (ObjectUtils.isEmpty(filterText))
            grid.setItems(gameService.getAll());
        else
            grid.setItems(gameService.getAllByNameLike(filterText));
    }

}
