package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.dto.GameDTO;

@Service
public class GameBasicCallbackFetchProvider implements GameDataProviderHasCallbackFetch {

    private final GameService gameService;

    @Setter
    private String like;

    public GameBasicCallbackFetchProvider(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public CallbackDataProvider.FetchCallback<GameDTO, Void> get() {
        return query -> {
            if (ObjectUtils.isEmpty(like))
                return gameService.fetch(query.getOffset(), query.getLimit()).stream();
            else
                return gameService.fetch(like, query.getOffset(), query.getLimit()).stream();
        };
    }

}
