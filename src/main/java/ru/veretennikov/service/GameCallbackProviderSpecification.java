package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.domain.Game;
import ru.veretennikov.dto.GameDTO;
import ru.veretennikov.repository.GameSpecification;

import java.util.stream.Stream;

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

            if (ObjectUtils.isEmpty(filter.getLike()))
                return gameService.fetch(specification, query.getOffset(), query.getLimit(), getSort(query.getSortOrders())).stream();
            else
//                https://stackoverflow.com/questions/26379522/can-i-combine-a-query-definition-with-a-specification-in-a-spring-data-jpa-repo - problem doesn't decisioned
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

    private void initSpecification() {
        Specification<Game> newSpec = GameSpecification.getEmpty();
        if (!ObjectUtils.isEmpty(filter.getLike()))
            newSpec = newSpec.and(GameSpecification.quickSearch(filter.getLike()));
        specification = newSpec;
    }

}
