package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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
            initFilters();
            return (int) gameService.count(specification);
        };
    }

    @Override
    public CallbackDataProvider.FetchCallback<GameDTO, Void> getFetchCallback() {
        return query -> {
            initFilters();
            return gameService.fetch(specification, query.getOffset(), query.getLimit()).stream();
        };
    }

    private void initFilters() {
        Specification<Game> newSpec = GameSpecification.getEmpty();
        if (ObjectUtils.isNotEmpty(like))
            newSpec = newSpec.and(GameSpecification.quickSearch(like));
        specification = newSpec;
    }

}
