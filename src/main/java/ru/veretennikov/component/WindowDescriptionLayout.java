package ru.veretennikov.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class WindowDescriptionLayout extends HorizontalLayout {

    public WindowDescriptionLayout(String windowDescription, ComponentEventListener<ClickEvent<Button>> onCloseClickListener) {
        setPadding(false);
        setMargin(false);
        setWidthFull();

        Label windowDescriptionLabel = new Label(windowDescription);
        windowDescriptionLabel.getStyle().set("font-weight", "bold");
        windowDescriptionLabel.getStyle().set("text-align", "center");
        expand(windowDescriptionLabel);
        add(windowDescriptionLabel);

        setVerticalComponentAlignment(Alignment.CENTER, windowDescriptionLabel);

        Button closeButton = new Button();
        closeButton.setIcon(VaadinIcon.CLOSE.create());
        closeButton.addClickListener(onCloseClickListener);
        add(closeButton);
    }

}
