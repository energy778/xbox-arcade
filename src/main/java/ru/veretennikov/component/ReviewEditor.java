package ru.veretennikov.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import ru.veretennikov.domain.Game;
import ru.veretennikov.domain.Review;
import ru.veretennikov.service.GameService;
import ru.veretennikov.service.ReviewService;

import java.util.UUID;

import static java.util.Objects.isNull;

@SpringComponent
@UIScope
public class ReviewEditor extends VerticalLayout {

    private final ReviewService service;
    private final GameService gameService;

    @Setter
    private UUID gameId;
    private Review currentReview;
    private ChangeHandler changeHandler;

    DatePicker dateUpdatedField = new DatePicker("Date");
    TextArea noteField = new TextArea("Note");

    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    Binder<Review> binder = new Binder<>(Review.class);

    @Autowired
    public ReviewEditor(ReviewService service, GameService gameService) {
        this.service = service;
        this.gameService = gameService;

        initFields();
        setBinding();

        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> edit(currentReview.getId()));
        setVisible(false);
    }

    private void initFields() {
        dateUpdatedField.setClearButtonVisible(true);
        noteField.setWidthFull();
        noteField.setClearButtonVisible(true);
        add(dateUpdatedField, noteField, actions);
    }

    private void setBinding() {
        binder.bind(dateUpdatedField, Review::getDateUpdated, Review::setDateUpdated);
        binder.bind(noteField, Review::getNote, Review::setNote);
    }

    void save() {
        service.save(currentReview);
        changeHandler.onChange();
    }

    void delete() {
        if (isNull(currentReview.getId()))
            return;
        service.delete(currentReview.getId());
        changeHandler.onChange();
    }

    public final void edit(Integer id) {
        Game game = gameService.getOne(gameId);
        if (id == null)
            currentReview = Review.builder().game(game).build();
        else
            currentReview = service.getById(id).orElse(Review.builder().game(game).build());

        final boolean persisted = currentReview != null;

        cancel.setVisible(persisted);
        delete.setVisible(persisted);

        binder.setBean(currentReview);

        setVisible(true);

        dateUpdatedField.focus();
    }

    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }

}
