package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.dto.GameDTO;

import java.util.stream.Stream;

@Service
public class GameCallbackProviderBasic extends GameCallbackProvider {

    private final GameService gameService;

    public GameCallbackProviderBasic(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public CallbackDataProvider.CountCallback<GameDTO, Void> getCountCallback() {
        return query -> {
            if (ObjectUtils.isEmpty(filter.getLike()))
                return (int) gameService.count();
            else
                return (int) gameService.count(filter.getLike());
        };
    }

    @Override
    public CallbackDataProvider.FetchCallback<GameDTO, Void> getFetchCallback() {
        return query -> {
            if (ObjectUtils.isEmpty(filter.getLike()))
                return gameService.fetch(query.getOffset(), query.getLimit(), getSort(query.getSortOrders())).stream();
            else
                return gameService.fetch(filter.getLike(), query.getOffset(), query.getLimit(), getSort(query.getSortOrders())).stream();
        };
    }

    @Override
    public Stream<String> fetchRatings() {
        return Stream.empty();
    }

    @Override
    public Stream<String> fetchDevelopers() {
        return Stream.empty();
    }

    @Override
    public Stream<String> fetchPublishers() {
        return Stream.empty();
    }

}
