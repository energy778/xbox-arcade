package ru.veretennikov.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.component.GameEditDialog;
import ru.veretennikov.component.GameEditor;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.service.GameService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Route
public class MainView extends VerticalLayout {

//    region fields
    private final GameService gameService;
//    endregion

//    region components
    final Grid<GameDTO> grid;
    final TextField filter;
    final Button addNewBtn;
    private final GameEditDialog gameEditDialog;
//    endregion

    public MainView(GameService gameService, GameEditor editor) {
        this.gameService = gameService;
        this.gameEditDialog = new GameEditDialog(editor);
        this.grid = new Grid<>(GameDTO.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New game", VaadinIcon.PLUS.create());

        // build layout
        setHeightFull();
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        add(actions, grid);

        grid.removeAllColumns();

        grid.addColumn(item -> "").setKey("rowIndex").setHeader("№");
        grid.getColumnByKey("rowIndex").getElement()
                .executeJs("this.renderer = function(root, column, rowData) {root.textContent = rowData.index + 1}");

        grid.addComponentColumn(gameDTO -> new Image(Optional.ofNullable(gameDTO.getPicUrl())
                .map(s -> {
                    try {
                        return new URL(s);
                    } catch (MalformedURLException ignored) {}
                    return null;
                })
                .map(URL::toString)
                .orElse(null), "screen"));

        grid.addColumns("name", "releaseDate", "rating", "price", "developer", "publisher");
        grid.getColumnByKey("name").setWidth("17em");

        grid.addComponentColumn(gameDTO -> {
            Checkbox checkbox = new Checkbox(!gameDTO.getPicUrl().isBlank());
            checkbox.setEnabled(false);
            return checkbox;
        }).setHeader("pic").setWidth("1em");

        grid.addComponentColumn(gameDTO -> {
            Checkbox checkbox = new Checkbox(gameDTO.isAvailability());
            checkbox.setEnabled(false);
            return checkbox;}
        ).setHeader("✔").setWidth("1em");

        filter.setPlaceholder("Filter by name (like ignore case)");
        filter.setSuffixComponent(new Label("Press ALT + 1 to focus"));
        filter.setClearButtonVisible(true);
        filter.setWidth("31em");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addFocusShortcut(Key.DIGIT_1, KeyModifier.ALT);
        filter.addValueChangeListener(e -> refreshGridSource(e.getValue()));

        grid.addItemDoubleClickListener(selectionEvent -> {
            gameEditDialog.setIdCurrentGame(selectionEvent.getItem().getId());
            gameEditDialog.open();
        });

        // Instantiate and edit new Game the new button is clicked
        addNewBtn.addClickListener(e -> {
            gameEditDialog.setIdCurrentGame(null);
            gameEditDialog.open();
        });

        // Listen changes made by the edit dialog, refresh data from backend
        gameEditDialog.setChangeHandler(() -> refreshGridSource(filter.getValue()));
        gameEditDialog.setDeleteHandler(() -> refreshGridSource(filter.getValue()));

        // Initialize listing
        refreshGridSource(null);

    }

    void refreshGridSource(String filterText) {
        if (ObjectUtils.isEmpty(filterText))
            grid.setItems(gameService.getAll());
        else
            grid.setItems(gameService.getAllByNameLike(filterText));
    }

}
