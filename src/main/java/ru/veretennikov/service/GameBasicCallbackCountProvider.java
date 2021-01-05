package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.dto.GameDTO;

@Service
public class GameBasicCallbackCountProvider implements GameDataProviderHasCallbackCount {

    private final GameService gameService;

    @Setter
    private String like;

    public GameBasicCallbackCountProvider(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public CallbackDataProvider.CountCallback<GameDTO, Void> get() {
        return query -> {
            if (ObjectUtils.isEmpty(like))
                return (int) gameService.count();
            else
                return (int) gameService.count(like);
        };
    }

}
