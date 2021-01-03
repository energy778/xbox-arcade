package ru.veretennikov.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.component.GameEditor;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.dto.GameWithDetailsDTO;
import ru.veretennikov.service.GameService;

@Route
public class MainView extends VerticalLayout {

//    region fields
    private final GameService gameService;
    private final GameEditor editor;
//    endregion

//    region components
    final Grid<GameDTO> grid;
    final TextField filter;
    final Button addNewBtn;
//    endregion

    public MainView(GameService gameService, GameEditor editor) {
        this.gameService = gameService;
        this.editor = editor;
        this.grid = new Grid<>(GameDTO.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New game", VaadinIcon.PLUS.create());

        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        add(actions, grid, editor);

        grid.setHeight("300px");
        grid.setColumns("name", "picUrl", "releaseDate", "rating", "price", "availability", "developer", "publisher");
        grid.getColumnByKey("name").setWidth("50px");

//        grid.addColumn(item -> "").setKey("rowIndex")
//                .setHeader("№")
//                .setAutoWidth(true);
//        grid.getColumnByKey("rowIndex").getElement()
//                .executeJs("this.renderer = function(root, column, rowData) {root.textContent = rowData.index + 1}");

        filter.setPlaceholder("Filter by name (like ignore case)");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> {
            editor.setVisible(false);
            refreshGridSource(e.getValue());
        });

        // Connect selected Game to editor or hide if none is selected

        /* only mouse: */
//        grid.addItemClickListener(gameDTOItemClickEvent -> editor.editGame(gameDTOItemClickEvent.getItem()));
        /* mouse and keyboard: */
        grid.addSelectionListener(selectionEvent -> selectionEvent.getFirstSelectedItem().map(GameDTO::getId).ifPresent(editor::editGame));
        /* alternative: */
//        grid.asSingleSelect().addValueChangeListener(e -> editor.editGame(e.getValue()));

        // Instantiate and edit new Game the new button is clicked
        addNewBtn.addClickListener(e -> editor.editGame(GameWithDetailsDTO.builder().build().getId()));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            refreshGridSource(filter.getValue());
        });

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
