package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import ru.veretennikov.dto.GameDTO;

public interface GameDataProviderHasCallbackFetch {
    CallbackDataProvider.FetchCallback<GameDTO, Void> get();
    void setLike(String like);
}
