package ru.veretennikov.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import ru.veretennikov.domain.Review;
import ru.veretennikov.service.ReviewService;

import java.util.UUID;

public class ReviewDialog extends Dialog {

    private final WindowDescriptionLayout windowDescriptionLayout;
    private UUID idCurrentGame;
    private String nameCurrentGame;

    private final ReviewService reviewService;
    final Grid<Review> grid;
    final Button addNewBtn;

    public ReviewDialog(ReviewService reviewService, ReviewEditor editor) {
        this.reviewService = reviewService;

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnOutsideClick(false);

        windowDescriptionLayout = new WindowDescriptionLayout(getWindowDescription(), closeEvent -> this.close());
        add(windowDescriptionLayout);

        this.grid = new Grid<>(Review.class);
        this.addNewBtn = new Button("New review", VaadinIcon.PLUS.create());

        HorizontalLayout actions = new HorizontalLayout(addNewBtn);
        add(actions, grid, editor);

        setWidth("70em");
        grid.setHeight("300px");
        grid.setColumns("dateUpdated", "note");

        grid.addSelectionListener(selectionEvent -> selectionEvent.getFirstSelectedItem().map(Review::getId).ifPresent(id -> {
            editor.setGameId(idCurrentGame);
            editor.edit(id);
        }));
        addNewBtn.addClickListener(e -> {
            editor.setGameId(idCurrentGame);
            editor.edit(null);
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            refreshGridSource();
        });

        refreshGridSource();
    }

    public void setCurrentGame(UUID id, String name) {
        this.idCurrentGame = id;
        this.nameCurrentGame = name;
        windowDescriptionLayout.setWindowDescription(getWindowDescription());
        refreshGridSource();
    }

    private String getWindowDescription() {
        return String.format("Reviews for %s", nameCurrentGame);
    }

    private void refreshGridSource() {
        grid.setItems(reviewService.findAllByGameId(idCurrentGame));
    }

}
