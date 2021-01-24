package ru.veretennikov.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
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
import ru.veretennikov.component.ReviewDialog;
import ru.veretennikov.component.ReviewEditor;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.dto.GameDTO.GameFilter;
import ru.veretennikov.service.FavouriteGameService;
import ru.veretennikov.service.GameCallbackProvider;
import ru.veretennikov.service.ReviewService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Route
@CssImport(value = "./theming/grid-main.css", themeFor="vaadin-grid")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
public class MainView extends VerticalLayout {

//    region поля
    private GameFilter filter;
//    endregion

//    region fields
    private final GameCallbackProvider gameCallbackProvider;
    private final FavouriteGameService favouriteGameService;
    private final CallbackDataProvider<GameDTO, Void> lazyDataProvider;
//    endregion

//    region components
    private final Grid<GameDTO> grid;
    private final TextField searchFilter;
    private final Button addNewBtn;
    private final GameEditDialog gameEditDialog;
    private final ReviewDialog reviewDialog;
    private Checkbox allowEdit;
    private TextField nameFilterField;
    private CheckboxGroup<Boolean> favouriteFilterField;
    private CheckboxGroup<Boolean> picAvailableFilterField;
    private CheckboxGroup<Boolean> availableFilterField;
    private DatePicker releaseDateFromFilterField;
    private DatePicker releaseDateToFilterField;
    private IntegerField priceFromFilterField;
    private IntegerField priceToFilterField;
    private Select<String> developersFilterField;
    private Select<String> publisherFilterField;
    private Select<String> ratingFilterField;
    //    endregion

    public MainView(GameEditor editor,
                    ReviewEditor reviewEditor,
                    @Qualifier("gameCallbackProviderQueryDSL") GameCallbackProvider gameCallbackProvider,
                    FavouriteGameService favouriteGameService,
                    ReviewService reviewService) {
        this.gameCallbackProvider = gameCallbackProvider;
        this.favouriteGameService = favouriteGameService;
        this.gameEditDialog = new GameEditDialog(editor);
        this.reviewDialog = new ReviewDialog(reviewService, reviewEditor);
        this.grid = new Grid<>(GameDTO.class);
        this.searchFilter = new TextField();
        this.addNewBtn = new Button("New game", VaadinIcon.PLUS.create());
        this.lazyDataProvider = DataProvider.fromCallbacks(gameCallbackProvider.getFetchCallback(), gameCallbackProvider.getCountCallback());

        gridInit();
        actionsInit();

        // build layout
        setHeightFull();
        HorizontalLayout actions = new HorizontalLayout(searchFilter, addNewBtn, allowEdit);
        actions.setVerticalComponentAlignment(Alignment.CENTER, allowEdit);
        add(actions, grid);

        // Initialize listing
        gameEditDialog.setEditable(allowEdit.getValue());

        refreshGridSource();
    }

    void refreshGridSource() {
        refreshFilters();
        gameCallbackProvider.setFilter(filter);
        lazyDataProvider.refreshAll();
    }

    private void gridInit() {
        grid.removeAllColumns();
        HeaderRow filterRow = grid.appendHeaderRow();

        Grid.Column<GameDTO> favouriteColumn = grid.addColumn(item -> "")
                .setKey("favourite")
                .setAutoWidth(true)
                .setSortProperty("favourite")
                .setClassNameGenerator(gameDTO -> {
                    if (gameDTO.isFavorite())
                        return "favourite";
                    else
                        return "unfavourite";
                });

        grid.addComponentColumn(gameDTO -> new Button(VaadinIcon.COMMENT_ELLIPSIS.create(), event -> {
            reviewDialog.setCurrentGame(gameDTO.getId(), gameDTO.getName());
            reviewDialog.open();
        }))
                .setAutoWidth(true)
                .setResizable(false);

        grid.addColumn(item -> "")
                .setKey("rowIndex")
                .setHeader("№")
                .setAutoWidth(true)
                .setResizable(true);
        grid.getColumnByKey("rowIndex").getElement()
                .executeJs("this.renderer = function(root, column, rowData) {root.textContent = rowData.index + 1}");

        Grid.Column<GameDTO> nameColumn = grid.addColumn("name").setWidth("17em").setAutoWidth(true).setResizable(true);
        Grid.Column<GameDTO> releaseDateColumn = grid.addColumn("releaseDate").setAutoWidth(true).setResizable(true);
        Grid.Column<GameDTO> ratingColumn = grid.addColumn("rating").setAutoWidth(true).setResizable(true);
        Grid.Column<GameDTO> priceColumn = grid.addComponentColumn(gameDTO -> {
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
        Grid.Column<GameDTO> developerColumn = grid.addColumn("developer").setAutoWidth(true).setResizable(true);
        Grid.Column<GameDTO> publisherColumn = grid.addColumn("publisher").setAutoWidth(true).setResizable(true);

        grid.setClassNameGenerator(gameDTO -> {
            if (gameDTO.isAvailability())
                return "available";
            return "unavailable";
        });

        Grid.Column<GameDTO> picAvailableColumn = grid.addComponentColumn(gameDTO -> {
            Checkbox checkbox = new Checkbox(!ObjectUtils.isEmpty(gameDTO.getPicUrl()));
            checkbox.setEnabled(false);
            return checkbox;
        })
                .setHeader("Pic")
                .setAutoWidth(true)
                .setResizable(true)
                .setSortProperty("pic");

        Grid.Column<GameDTO> availableColumn = grid.addComponentColumn(gameDTO -> {
            Checkbox checkbox = new Checkbox(gameDTO.isAvailability());
            checkbox.setEnabled(false);
            return checkbox;
        })
                .setHeader("✔")
                .setAutoWidth(true)
                .setResizable(true)
                .setSortProperty("available");

//        region
        favouriteFilterField = new CheckboxGroup<>();
        favouriteFilterField.setItems(true, false);
        favouriteFilterField.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        favouriteFilterField.addValueChangeListener(event -> refreshGridSource());
        filterRow.getCell(favouriteColumn).setComponent(favouriteFilterField);

        nameFilterField = new TextField();
        nameFilterField.addValueChangeListener(event -> refreshGridSource());
        nameFilterField.setSizeFull();
        nameFilterField.setPlaceholder(" ... like ... ");
        nameFilterField.getElement().setAttribute("theme", "small");
        filterRow.getCell(nameColumn).setComponent(nameFilterField);

        releaseDateFromFilterField = new DatePicker(event -> refreshGridSource());
        releaseDateFromFilterField.setPlaceholder("release date from");
        releaseDateFromFilterField.setSizeFull();
        releaseDateFromFilterField.setClearButtonVisible(true);
        releaseDateFromFilterField.getElement().setAttribute("theme", "small");
        releaseDateToFilterField = new DatePicker(event -> refreshGridSource());
        releaseDateToFilterField.setPlaceholder("release date to");
        releaseDateToFilterField.setSizeFull();
        releaseDateToFilterField.setClearButtonVisible(true);
        releaseDateToFilterField.getElement().setAttribute("theme", "small");
        VerticalLayout releaseDateVl = new VerticalLayout(releaseDateFromFilterField, releaseDateToFilterField);
        releaseDateVl.getElement().setAttribute("theme", "small");
        filterRow.getCell(releaseDateColumn).setComponent(releaseDateVl);

        ratingFilterField = new Select<>();
        ratingFilterField.setItems(gameCallbackProvider.fetchRatings());
        ratingFilterField.setEmptySelectionAllowed(true);
        ratingFilterField.setEmptySelectionCaption("<empty>");
        ratingFilterField.addValueChangeListener(event -> refreshGridSource());
        ratingFilterField.getElement().setAttribute("theme", "small");
        filterRow.getCell(ratingColumn).setComponent(ratingFilterField);

        priceFromFilterField = new IntegerField(event -> refreshGridSource());
        priceFromFilterField.setPlaceholder("price from");
        priceFromFilterField.setSizeFull();
        priceFromFilterField.setClearButtonVisible(true);
        priceFromFilterField.getElement().setAttribute("theme", "small");
        priceToFilterField = new IntegerField(event -> refreshGridSource());
        priceToFilterField.setPlaceholder("price to");
        priceToFilterField.setSizeFull();
        priceToFilterField.setClearButtonVisible(true);
        priceToFilterField.getElement().setAttribute("theme", "small");
        VerticalLayout priceVl = new VerticalLayout(priceFromFilterField, priceToFilterField);
        priceVl.getElement().setAttribute("theme", "small");
        filterRow.getCell(priceColumn).setComponent(priceVl);

        developersFilterField = new Select<>();
        developersFilterField.setItems(gameCallbackProvider.fetchDevelopers());
        developersFilterField.setEmptySelectionAllowed(true);
        developersFilterField.setEmptySelectionCaption("<empty>");
        developersFilterField.addValueChangeListener(event -> refreshGridSource());
        developersFilterField.getElement().setAttribute("theme", "small");
        filterRow.getCell(developerColumn).setComponent(developersFilterField);

        publisherFilterField = new Select<>();
        publisherFilterField.setItems(gameCallbackProvider.fetchPublishers());
        publisherFilterField.setEmptySelectionAllowed(true);
        publisherFilterField.setEmptySelectionCaption("<empty>");
        publisherFilterField.addValueChangeListener(event -> refreshGridSource());
        publisherFilterField.getElement().setAttribute("theme", "small");
        filterRow.getCell(publisherColumn).setComponent(publisherFilterField);

        picAvailableFilterField = new CheckboxGroup<>();
        picAvailableFilterField.setItems(true, false);
        picAvailableFilterField.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        picAvailableFilterField.addValueChangeListener(event -> refreshGridSource());
        filterRow.getCell(picAvailableColumn).setComponent(picAvailableFilterField);

        availableFilterField = new CheckboxGroup<>();
        availableFilterField.setItems(true, false);
        availableFilterField.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        availableFilterField.addValueChangeListener(event -> refreshGridSource());
        filterRow.getCell(availableColumn).setComponent(availableFilterField);
//        endregion

        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addItemClickListener(event -> onClick(event.getColumn(), event.getItem()));
        grid.addItemDoubleClickListener(selectionEvent -> {
            gameEditDialog.setIdCurrentGame(selectionEvent.getItem().getId());
            gameEditDialog.open();
        });

        // Listen changes made by the edit dialog, refresh data from backend
        gameEditDialog.setChangeHandler(this::refreshGridSource);
        gameEditDialog.setDeleteHandler(this::refreshGridSource);

        grid.setMultiSort(true);
        grid.setDataProvider(lazyDataProvider);
    }

    private void actionsInit() {
        allowEdit = new Checkbox("Allow edit game", event -> {
            addNewBtn.setVisible(event.getValue());
            gameEditDialog.setEditable(event.getValue());
        });
        addNewBtn.setVisible(false);    allowEdit.setVisible(false);    // TODO: 05.01.21 until security don`t have

        searchFilter.setPlaceholder("Filter by name and description (like ignore case)");
        searchFilter.setSuffixComponent(new Label("Press ALT + 1 to focus"));
        searchFilter.setClearButtonVisible(true);
        searchFilter.setWidth("37em");

        // Replace listing with filtered content when user changes filter
        searchFilter.setValueChangeMode(ValueChangeMode.LAZY);
        searchFilter.addFocusShortcut(Key.DIGIT_1, KeyModifier.ALT);
        searchFilter.addValueChangeListener(e -> refreshGridSource());

        // Instantiate and edit new Game the new button is clicked
        addNewBtn.addClickListener(e -> {
            gameEditDialog.setIdCurrentGame(null);
            gameEditDialog.open();
        });
    }

    private void onClick(Grid.Column<GameDTO> column, GameDTO item) {
        if ("favourite".equals(column.getKey()))
            favoritesProceed(item);
    }

    private void favoritesProceed(GameDTO item) {
        String notification;
        if (item.isFavorite()) {
            notification = "remove from favourites";
            favouriteGameService.delete(item.getId());
        } else {
            notification = "add to favourites";
            favouriteGameService.add(item.getId());
        }
        item.setFavorite(!item.isFavorite());
        grid.getDataCommunicator().refresh(item);
        Notification.show(String.format("%s: %s", notification, item.getName()));
    }

    private void refreshFilters() {
        Set<String> ratings = new HashSet<>();
        Set<String> developers = new HashSet<>();
        Set<String> publishers = new HashSet<>();
        Optional.ofNullable(ratingFilterField.getValue()).ifPresent(ratings::add);
        Optional.ofNullable(developersFilterField.getValue()).ifPresent(developers::add);
        Optional.ofNullable(publisherFilterField.getValue()).ifPresent(publishers::add);
        // TODO: 025 25.01.21 ratingIds, developerIds, publisherIds -> multiple choice
        filter = GameFilter.builder()
                .like(searchFilter.getValue())
                .favourite(favouriteFilterField.getValue())
                .likeName(nameFilterField.getValue())
                .releaseDateFrom(releaseDateFromFilterField.getValue())
                .releaseDateTo(releaseDateToFilterField.getValue())
                .ratingIds(ratings)
                .priceFrom(priceFromFilterField.getValue())
                .priceTo(priceToFilterField.getValue())
                .developerIds(developers)
                .publisherIds(publishers)
                .picAvailable(picAvailableFilterField.getValue())
                .available(availableFilterField.getValue())
                .build();
    }

}
