package ru.veretennikov.service;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.domain.Game;
import ru.veretennikov.dto.GameDTO;

import java.util.List;
import java.util.stream.Stream;

public abstract class GameCallbackProvider {

    @Setter
    protected String like;

    public abstract CallbackDataProvider.CountCallback<GameDTO, Void> getCountCallback();
    public abstract CallbackDataProvider.FetchCallback<GameDTO, Void> getFetchCallback();

    protected Sort getSort(List<QuerySortOrder> sortOrders) {
        /* primitive */
//            Sort sort = Sort.by(sortOrders.getSortOrders().stream()
//                    .map(item -> new Sort.Order(getSortDirection(item.getDirection()), item.getSorted()))
//                    .toArray(Sort.Order[]::new));

        Sort.TypedSort<Game> typedSortGameTemplate = Sort.sort(Game.class);
        Sort sort = Sort.by(sortOrders.stream()
                .flatMap(item -> {
                    String sorted = item.getSorted();
                    if (GameDTO.Fields.name.toString().equals(sorted)
                            || GameDTO.Fields.releaseDate.toString().equals(sorted)
                            || GameDTO.Fields.rating.toString().equals(sorted)
                            || GameDTO.Fields.price.toString().equals(sorted)
                            || GameDTO.Fields.developer.toString().equals(sorted)
                            || GameDTO.Fields.publisher.toString().equals(sorted))
                        return Stream.ofNullable(new Sort.Order(getSortDirection(item.getDirection()), item.getSorted(), getNullHandlingByDirection(item.getDirection())));
                    /* custom columns */
                    if ("pic".equals(sorted))
                        return addTypedSortDirection(typedSortGameTemplate.by((Game game) -> !ObjectUtils.isEmpty(game.getPicUrl())), item.getDirection()).get();
                    else if ("available".equals(sorted))
                        return addTypedSortDirection(typedSortGameTemplate.by((Game game) -> game.getAvailability() != null && game.getAvailability()), item.getDirection()).get();        // field type must be not primitive
                    else if ("favourite".equals(sorted))
                        return Stream.ofNullable(new Sort.Order(getSortDirection(item.getDirection()), "f.id", getNullHandlingByDirection(item.getDirection())));
//                        return Stream.ofNullable(new Sort.Order(getSortDirection(item.getDirection()), "case when f.id is null then false else true end", getNullHandlingByDirection(item.getDirection())));  not supported
//                        return JpaSort.unsafe(getSortDirection(item.getDirection()), "case when f.id is null then false else true end").get();                                                                not supported
                    else
                        return Stream.empty();
                })
                .toArray(Sort.Order[]::new));

        if (sort.isUnsorted() || sort.isEmpty())
            return Sort.by(new Sort.Order(Sort.Direction.ASC, GameDTO.Fields.name.toString(), Sort.NullHandling.NULLS_FIRST));

        return sort;
    }

    private Sort addTypedSortDirection(Sort.TypedSort<?> currentTypedSort, SortDirection direction) {
        return SortDirection.DESCENDING.equals(direction) ? currentTypedSort.descending() : currentTypedSort.ascending();
    }

    private Sort.Direction getSortDirection(SortDirection direction) {
        return SortDirection.DESCENDING.equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    private Sort.NullHandling getNullHandlingByDirection(SortDirection direction) {
//        https://stackoverflow.com/questions/3683174/hibernate-order-by-with-nulls-last
        return SortDirection.DESCENDING.equals(direction) ? Sort.NullHandling.NULLS_LAST : Sort.NullHandling.NULLS_FIRST;
    }

}
