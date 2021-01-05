package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import lombok.Setter;
import ru.veretennikov.dto.GameDTO;

public abstract class GameCallbackProvider {
    @Setter
    protected String like;

    public abstract CallbackDataProvider.CountCallback<GameDTO, Void> getCountCallback();
    public abstract CallbackDataProvider.FetchCallback<GameDTO, Void> getFetchCallback();
}
