package ru.veretennikov.component;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import ru.veretennikov.dto.GameWithDetailsDTO;
import ru.veretennikov.service.GameService;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@SpringComponent
@UIScope
public class GameEditor extends VerticalLayout implements KeyNotifier {

    public static final Locale DATE_PICKER_LOCALE = new Locale("ru");
    public static final DatePicker.DatePickerI18n DATE_PICKER_I18N = new DatePicker.DatePickerI18n()
            .setWeek("Неделя").setCalendar("Календарь").setClear("Очистить").setToday("Сегодня").setCancel("Отмена").setFirstDayOfWeek(1)
            .setMonthNames(Arrays.asList("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"))
            .setWeekdays(Arrays.asList("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"))
            .setWeekdaysShort(Arrays.asList("вс", "пн", "вт", "ср", "чт", "пт", "сб"));

    private final GameService service;

    /**
     * The currently edited game
     */
    private GameWithDetailsDTO currentGame;

    /* Fields to edit properties in Game entity */
    TextField idField = new TextField("ID");
    TextField nameField = new TextField("Name");
    TextField gameUrlField = new TextField("Game url");
    TextField picUrlField = new TextField("Pic url");
    DatePicker releaseDateField = new DatePicker("Release date");
    TextArea description1Field = new TextArea("Description 1");
    TextArea description2Field = new TextArea("Description 2");
    TextField ratingField = new TextField("Rating");
    IntegerField priceField = new IntegerField("Price");
    TextField locationField = new TextField("Location");
    Checkbox availabilityField = new Checkbox("Availability");
    DatePicker dateIssueField = new DatePicker("Date issue");
    TextField developerField = new TextField("Developer");
    TextField publisherField = new TextField("Publisher");
    Image pic = new Image();
    Image currentScreen = new Image();
    Tabs screenTabs = new Tabs();
    Tabs genresTabs = new Tabs();

    /* Action buttons */
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    Binder<GameWithDetailsDTO> binder = new Binder<>();
    private ChangeHandler changeHandler;

    @Autowired
    public GameEditor(GameService service) {
        this.service = service;

        initFields();
        setBinding();

        // Configure and style components
        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editGame(currentGame.getId()));
        setVisible(false);
    }

    private void initFields() {
        idField.setWidth("31em");
        idField.setClearButtonVisible(true);
        nameField.setWidth("31em");
        nameField.setClearButtonVisible(true);

        gameUrlField.setWidth("31em");
        gameUrlField.setClearButtonVisible(true);
        picUrlField.setWidth("31em");
        picUrlField.setClearButtonVisible(true);

        description1Field.setWidth("100em");
        description1Field.setClearButtonVisible(true);
        description2Field.setWidth("100em");
        description2Field.setClearButtonVisible(true);

        ratingField.setWidth("15em");
        ratingField.setClearButtonVisible(true);
        priceField.setWidth("15em");
        priceField.setHasControls(true);
        priceField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        priceField.setSuffixComponent(new Label("₽"));
        priceField.setMin(0);

        locationField.setWidth("31em");
        locationField.setClearButtonVisible(true);

        Div dateReleaseMessage = new Div();
        releaseDateField.setWidthFull();
        releaseDateField.setClearButtonVisible(true);
        releaseDateField.setWeekNumbersVisible(true);
        releaseDateField.setI18n(DATE_PICKER_I18N);
        releaseDateField.setLocale(DATE_PICKER_LOCALE);
        releaseDateField.addValueChangeListener(event -> dateReleaseMessage.setText(getMessageI18nRU(event.getValue(), releaseDateField.getI18n())));
        VerticalLayout vlReleaseDate = new VerticalLayout(releaseDateField, dateReleaseMessage);
        vlReleaseDate.setWidth("15em");

        Div dateIssueMessage = new Div();
        dateIssueField.setWidthFull();
        dateIssueField.setClearButtonVisible(true);
        dateIssueField.setWeekNumbersVisible(true);
        dateIssueField.setI18n(DATE_PICKER_I18N);
        dateIssueField.setLocale(DATE_PICKER_LOCALE);
        dateIssueField.addValueChangeListener(event -> dateIssueMessage.setText(getMessageI18nRU(event.getValue(), dateIssueField.getI18n())));
        VerticalLayout vlDateIssue = new VerticalLayout(dateIssueField, dateIssueMessage);
        vlDateIssue.setWidth("15em");

        developerField.setWidth("15em");
        developerField.setClearButtonVisible(true);
        publisherField.setWidth("15em");
        publisherField.setClearButtonVisible(true);

        screenTabs.setAutoselect(false);
        screenTabs.setFlexGrowForEnclosedTabs(1);
        screenTabs.addSelectedChangeListener(this::refreshScreenshot);

        genresTabs.setEnabled(false);
        genresTabs.setAutoselect(false);
        genresTabs.setFlexGrowForEnclosedTabs(1);
        genresTabs.setOrientation(Tabs.Orientation.VERTICAL);

        /* grouping */
        Details descriptionDetails = new Details("Description", new FormLayout(new VerticalLayout(description1Field, description2Field)));
        Details otherDetails = new Details("Other", new VerticalLayout(
                new HorizontalLayout(ratingField, priceField),
                new HorizontalLayout(developerField, publisherField),
                new HorizontalLayout(vlReleaseDate, vlDateIssue)
        ));
        Details screensDetails = new Details("Screenshots", new VerticalLayout(screenTabs, currentScreen));

        add(new HorizontalLayout(pic, genresTabs),
            new FormLayout(idField, nameField, gameUrlField, locationField),
            availabilityField,
            descriptionDetails,
            otherDetails,
            screensDetails,
            actions);
    }

    @SuppressWarnings("DuplicatedCode")
    private void setBinding() {
        binder.bind(idField,
                gameWithDetailsDTO ->
                        Optional.ofNullable(gameWithDetailsDTO.getId())
                                .map(UUID::toString)
                                .orElse(null),
                (newValue, id) ->
                        newValue.setId(Optional.ofNullable(id)
                                .filter(s -> !s.isBlank())
                                // TODO: 04.01.21 need UUID validation and restore previous value
                                .map(UUID::fromString)
                                .orElse(null)));
        binder.bind(nameField, GameWithDetailsDTO::getName, GameWithDetailsDTO::setName);
        binder.bind(gameUrlField, GameWithDetailsDTO::getGameUrl, GameWithDetailsDTO::setGameUrl);
        /* alternative method */
        binder.forField(picUrlField).bind(GameWithDetailsDTO::getPicUrl, GameWithDetailsDTO::setPicUrl);
        binder.forField(releaseDateField).bind(GameWithDetailsDTO::getReleaseDate, GameWithDetailsDTO::setReleaseDate);
        binder.forField(description1Field).bind(GameWithDetailsDTO::getDescription1, GameWithDetailsDTO::setDescription1);
        binder.forField(description2Field).bind(GameWithDetailsDTO::getDescription2, GameWithDetailsDTO::setDescription2);
        binder.forField(ratingField).bind(GameWithDetailsDTO::getRating, GameWithDetailsDTO::setRating);
        binder.forField(priceField).bind(GameWithDetailsDTO::getPrice, GameWithDetailsDTO::setPrice);
        binder.forField(locationField).bind(GameWithDetailsDTO::getLocation, GameWithDetailsDTO::setLocation);
        binder.forField(availabilityField).bind(GameWithDetailsDTO::isAvailability, GameWithDetailsDTO::setAvailability);
        binder.forField(dateIssueField).bind(GameWithDetailsDTO::getDateIssue, GameWithDetailsDTO::setDateIssue);
        binder.forField(developerField).bind(GameWithDetailsDTO::getDeveloper, GameWithDetailsDTO::setDeveloper);
        binder.forField(publisherField).bind(GameWithDetailsDTO::getPublisher, GameWithDetailsDTO::setPublisher);
    }

    void save() {
        service.save(currentGame);
        changeHandler.onChange();
    }

    void delete() {
        service.delete(currentGame.getId());
        changeHandler.onChange();
    }

    public final void editGame(UUID id) {
        if (id == null)
            currentGame = GameWithDetailsDTO.builder().build();
        else
            currentGame = service.getByIdWithDetails(id).orElse(null);

        final boolean persisted = currentGame != null;

        initGenresTabs();
        refreshPic();
        currentScreen.setSrc("");
        initScreenTabs();

        cancel.setVisible(persisted);
        delete.setVisible(persisted);

        // Bind game properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(currentGame);

        setVisible(true);

        // Focus first name initially
        idField.focus();
    }

    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }

    private String getMessageI18nRU(LocalDate selectedDate, DatePicker.DatePickerI18n datePickerI18n) {
        if (selectedDate == null)
            return "Дата не выбрана";

        int weekday = selectedDate.getDayOfWeek().getValue() % 7;
        String weekdayName = datePickerI18n.getWeekdays().get(weekday);

        int month = selectedDate.getMonthValue() - 1;
        String monthName = datePickerI18n.getMonthNames().get(month);

        return "День недели: " + weekdayName + ", месяц: " + monthName;
    }

    private void refreshPic() {
        Optional<String> picUrlOpt = Optional.ofNullable(currentGame).map(GameWithDetailsDTO::getPicUrl);
        if (picUrlOpt.isEmpty())
            pic.setSrc("");
        else {
            try {
                pic.setSrc(new URL(picUrlOpt.get()).toString());
            } catch (MalformedURLException ignored) {}
        }
    }

    private void initScreenTabs() {
        screenTabs.removeAll();
        Stream.ofNullable(currentGame)
                .map(GameWithDetailsDTO::getScreens)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(gameScreen -> screenTabs.add(new Tab(gameScreen.getName())));
    }

    private void initGenresTabs() {
        genresTabs.removeAll();
        Stream.ofNullable(currentGame)
                .map(GameWithDetailsDTO::getGenres)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(gameGenre -> genresTabs.add(new Tab(gameGenre.getName())));
    }

    private void refreshScreenshot(Tabs.SelectedChangeEvent selectedChangeEvent) {
        currentScreen.setSrc(Optional.ofNullable(currentGame)
                .map(game -> Stream.ofNullable(game.getScreens())
                        .flatMap(Collection::stream)
                        .filter(gameScreen -> selectedChangeEvent.getSelectedTab() != null
                                && selectedChangeEvent.getSelectedTab().getLabel() != null
                                && selectedChangeEvent.getSelectedTab().getLabel().equals(gameScreen.getName()))
                        .map(GameWithDetailsDTO.GameScreenDTO::getUrl)
                        .map(urlString -> {
                            try {
                                return new URL(urlString).toString();
                            } catch (MalformedURLException ignored) {}
                            return "";
                        })
                        .findAny()
                        .orElse(""))
                .orElse(""));
    }

}
