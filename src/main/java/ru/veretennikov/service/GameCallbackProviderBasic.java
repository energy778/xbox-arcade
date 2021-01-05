package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.dto.GameDTO;

@Service
public class GameCallbackProviderBasic extends GameCallbackProvider {

    private final GameService gameService;

    public GameCallbackProviderBasic(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public CallbackDataProvider.CountCallback<GameDTO, Void> getCountCallback() {
        return query -> {
            if (ObjectUtils.isEmpty(like))
                return (int) gameService.count();
            else
                return (int) gameService.count(like);
        };
    }

    @Override
    public CallbackDataProvider.FetchCallback<GameDTO, Void> getFetchCallback() {
        return query -> {
            if (ObjectUtils.isEmpty(like))
                return gameService.fetch(query.getOffset(), query.getLimit()).stream();
            else
                return gameService.fetch(like, query.getOffset(), query.getLimit()).stream();
        };
    }

}
