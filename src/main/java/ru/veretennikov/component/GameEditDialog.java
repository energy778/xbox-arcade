package ru.veretennikov.component;

import com.vaadin.flow.component.dialog.Dialog;

import java.util.UUID;

public class GameEditDialog extends Dialog {

    private final GameEditor editor;

    public GameEditDialog(GameEditor editor) {
        this.editor = editor;

        setModal(true);
        setDraggable(true);
        setResizable(true);
        setCloseOnOutsideClick(false);

        add(new WindowDescriptionLayout("Game edit form", closeEvent -> this.close()));
        add(editor);
    }

    public void setIdCurrentGame(UUID id) {
        editor.editGame(id);
    }

    public void setChangeHandler(GameEditor.ChangeHandler h) {
        editor.setChangeHandler(h);
    }

    public void setDeleteHandler(GameEditor.ChangeHandler h) {
        editor.setDeleteHandler(() -> {
            close();
            h.onChange();
        });
    }

}
