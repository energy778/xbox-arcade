package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.domain.Game;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.repository.GameSpecification;

@Primary
@Service
public class GameCallbackProviderSpecification extends GameCallbackProvider {

    private final GameService gameService;

    private Specification<Game> specification;

    public GameCallbackProviderSpecification(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public CallbackDataProvider.CountCallback<GameDTO, Void> getCountCallback() {
        return query -> {
            initSpecification();
            return (int) gameService.count(specification);
        };
    }

    @Override
    public CallbackDataProvider.FetchCallback<GameDTO, Void> getFetchCallback() {
        return query -> {
            initSpecification();
            return gameService.fetch(specification, query.getOffset(), query.getLimit(), getSort(query.getSortOrders())).stream();
        };
    }

    private void initSpecification() {
        Specification<Game> newSpec = GameSpecification.getEmpty();
        if (!ObjectUtils.isEmpty(like))
            newSpec = newSpec.and(GameSpecification.quickSearch(like));
        specification = newSpec;
    }

}
