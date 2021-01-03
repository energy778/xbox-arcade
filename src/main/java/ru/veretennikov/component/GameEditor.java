package ru.veretennikov.component;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import ru.veretennikov.dto.GameWithDetailsDTO;
import ru.veretennikov.service.GameService;

import java.util.Optional;
import java.util.UUID;

@SpringComponent
@UIScope
public class GameEditor extends VerticalLayout implements KeyNotifier {

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
    TextField description1Field = new TextField("Description 1");
    TextField description2Field = new TextField("Description 2");
    TextField ratingField = new TextField("Rating");
    IntegerField priceField = new IntegerField("Price");
    TextField locationField = new TextField("Location");
    Checkbox availabilityField = new Checkbox("Availability");
    DatePicker dateIssueField = new DatePicker("Date issue");
    TextField developerField = new TextField("Developer");
    TextField publisherField = new TextField("Publisher");

    /* Action buttons */
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    Binder<GameWithDetailsDTO> binder = new Binder<>(GameWithDetailsDTO.class);
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
        add(idField, nameField, gameUrlField, picUrlField, releaseDateField, description1Field,
                description2Field, ratingField, priceField, locationField, availabilityField,
                dateIssueField, developerField, publisherField,
                actions);
    }

    @SuppressWarnings("DuplicatedCode")
    private void setBinding() {
        binder.bind(idField,
                gameWithDetailsDTO ->
                        Optional.ofNullable(gameWithDetailsDTO.getId()).map(UUID::toString).orElse(null),
                (newValue, id) ->
                        newValue.setId(Optional.ofNullable(id).filter(s -> !s.isBlank()).map(UUID::fromString).orElse(null)));
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

}
