package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import ru.veretennikov.dto.GameDTO;

public interface GameDataProviderHasCallbackCount {
    CallbackDataProvider.CountCallback<GameDTO, Void> get();
    void setLike(String like);
}
