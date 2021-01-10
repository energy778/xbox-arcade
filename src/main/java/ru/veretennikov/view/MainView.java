package ru.veretennikov.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.component.GameEditDialog;
import ru.veretennikov.component.GameEditor;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.service.GameCallbackProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Route
@CssImport(value = "./theming/grid-main.css", themeFor="vaadin-grid")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
public class MainView extends VerticalLayout {

//    region fields
    private final GameCallbackProvider gameCallbackProvider;
//    endregion

//    region components
    final Grid<GameDTO> grid;
    final TextField filter;
    final Button addNewBtn;
    private final GameEditDialog gameEditDialog;
    private Checkbox allowEdit;
    private CallbackDataProvider<GameDTO, Void> lazyDataProvider;
//    endregion

    public MainView(GameEditor editor, @Qualifier("gameCallbackProviderQueryDSL") GameCallbackProvider gameCallbackProvider) {
        this.gameCallbackProvider = gameCallbackProvider;
        this.gameEditDialog = new GameEditDialog(editor);
        this.grid = new Grid<>(GameDTO.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New game", VaadinIcon.PLUS.create());

        gridInit();
        actionsInit();

        // build layout
        setHeightFull();
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn, allowEdit);
        actions.setVerticalComponentAlignment(Alignment.CENTER, allowEdit);
        add(actions, grid);

        // Initialize listing
        gameEditDialog.setEditable(allowEdit.getValue());

        refreshGridSource();
    }

    private void gridInit() {
        grid.removeAllColumns();

        grid.addColumn(item -> "")
                .setKey("rowIndex")
                .setHeader("№")
                .setAutoWidth(true)
                .setResizable(true);
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
                .orElse(""), "screen"));

        grid.addColumn("name").setWidth("17em").setAutoWidth(true).setResizable(true);
        grid.addColumn("releaseDate").setAutoWidth(true).setResizable(true);
        grid.addColumn("rating").setAutoWidth(true).setResizable(true);
        grid.addComponentColumn(gameDTO -> {
            if (gameDTO.getPrice() == null || gameDTO.getPrice() == 0)
                return new Label("Бесплатно");
            return new Label(gameDTO.getPrice().toString());
        }).setClassNameGenerator(gameDTO -> {
            if (gameDTO.getPrice() != null && gameDTO.getPrice() != 0)
                return "ruMoney";
            return "";
        })
                .setHeader("Price")
                .setSortProperty(GameDTO.Fields.price.toString())
                .setAutoWidth(true)
                .setResizable(true);
        grid.addColumn("developer").setAutoWidth(true).setResizable(true);
        grid.addColumn("publisher").setAutoWidth(true).setResizable(true);

        grid.setClassNameGenerator(gameDTO -> {
            if (gameDTO.isAvailability())
                return "available";
            return "unavailable";
        });

        grid.addComponentColumn(gameDTO -> {
            Checkbox checkbox = new Checkbox(!ObjectUtils.isEmpty(gameDTO.getPicUrl()));
            checkbox.setEnabled(false);
            return checkbox;
        })
                .setHeader("pic")
                .setAutoWidth(true)
                .setResizable(true)
                .setSortProperty("pic");

        grid.addComponentColumn(gameDTO -> {
            Checkbox checkbox = new Checkbox(gameDTO.isAvailability());
            checkbox.setEnabled(false);
            return checkbox;
        })
                .setHeader("✔")
                .setAutoWidth(true)
                .setResizable(true)
                .setSortProperty("available");

        grid.addItemDoubleClickListener(selectionEvent -> {
            gameEditDialog.setIdCurrentGame(selectionEvent.getItem().getId());
            gameEditDialog.open();
        });

        // Listen changes made by the edit dialog, refresh data from backend
        gameEditDialog.setChangeHandler(this::refreshGridSource);
        gameEditDialog.setDeleteHandler(this::refreshGridSource);

        grid.setMultiSort(true);

        lazyDataProvider = DataProvider.fromCallbacks(gameCallbackProvider.getFetchCallback(), gameCallbackProvider.getCountCallback());
        grid.setDataProvider(lazyDataProvider);
    }

    private void actionsInit() {
        allowEdit = new Checkbox("Allow edit game", event -> {
            addNewBtn.setVisible(event.getValue());
            gameEditDialog.setEditable(event.getValue());
        });
        addNewBtn.setVisible(false);    allowEdit.setVisible(false);    // TODO: 05.01.21 until security don`t have

        filter.setPlaceholder("Filter by name (like ignore case)");
        filter.setSuffixComponent(new Label("Press ALT + 1 to focus"));
        filter.setClearButtonVisible(true);
        filter.setWidth("31em");

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addFocusShortcut(Key.DIGIT_1, KeyModifier.ALT);
        filter.addValueChangeListener(e -> {
            gameCallbackProvider.setLike(e.getValue());
            refreshGridSource();
        });

        // Instantiate and edit new Game the new button is clicked
        addNewBtn.addClickListener(e -> {
            gameEditDialog.setIdCurrentGame(null);
            gameEditDialog.open();
        });
    }

    void refreshGridSource() {
        lazyDataProvider.refreshAll();
    }

}
