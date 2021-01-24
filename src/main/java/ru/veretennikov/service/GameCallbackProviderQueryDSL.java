package ru.veretennikov.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.domain.QFavouriteGame;
import ru.veretennikov.domain.QGame;
import ru.veretennikov.dto.GameDTO;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GameCallbackProviderQueryDSL extends GameCallbackProvider {

    private static final QGame t_game = QGame.game;
    private static final QFavouriteGame t_favouriteGame = QFavouriteGame.favouriteGame;
    private static final BooleanExpression FAVOURITE_EXPRESSION = new CaseBuilder().when(t_favouriteGame.id.isNull()).then(false).otherwise(true);
    private static final BooleanExpression PIC_AVAILABLE_EXPRESSION = new CaseBuilder().when(t_game.picUrl.isNull()).then(false).otherwise(true);
    private static final StringExpression RATING_EXPRESSION = new CaseBuilder().when(t_game.rating.isNull()).then("").otherwise(t_game.rating);
    private static final StringExpression DEVELOPER_EXPRESSION = new CaseBuilder().when(t_game.developer.isNull()).then("").otherwise(t_game.developer);
    private static final StringExpression PUBLISHER_EXPRESSION = new CaseBuilder().when(t_game.publisher.isNull()).then("").otherwise(t_game.publisher);

    private static final QTuple GAME_DTO_SELECT = Projections.tuple(t_game.id,
            t_game.name,
            t_game.gameUrl,
            t_game.picUrl,
            t_game.releaseDate,
            t_game.description1,
            t_game.description2,
            RATING_EXPRESSION,
            t_game.price,
            t_game.location,
            t_game.availability,
            t_game.dateIssue,
            DEVELOPER_EXPRESSION,
            PUBLISHER_EXPRESSION,
            FAVOURITE_EXPRESSION
    );
    private static final MappingProjection<GameDTO> gameGameDtoProjection = new MappingProjection<>(GameDTO.class, GAME_DTO_SELECT.getArgs().toArray(new Expression<?>[0])) {
        @Override
        protected GameDTO map(Tuple gameTuple) {
            return GameDTO.builder()
                        .id(gameTuple.get(t_game.id))
                        .name(gameTuple.get(t_game.name))
                        .picUrl(gameTuple.get(t_game.picUrl))
                        .releaseDate(gameTuple.get(t_game.releaseDate))
                        .rating(gameTuple.get(RATING_EXPRESSION))
                        .price(Optional.ofNullable(gameTuple.get(t_game.price)).orElse(0))
                        .availability(Optional.ofNullable(gameTuple.get(t_game.availability)).orElse(false))
                        .developer(gameTuple.get(DEVELOPER_EXPRESSION))
                        .publisher(gameTuple.get(PUBLISHER_EXPRESSION))
                        .favorite(Optional.ofNullable(gameTuple.get(FAVOURITE_EXPRESSION)).orElse(false))
                        .build();
        }
    };

    private final JPAQueryFactory queryFactory;

    public GameCallbackProviderQueryDSL(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public CallbackDataProvider.CountCallback<GameDTO, Void> getCountCallback() {
        return query -> (int) queryFactory.from(t_game)
                .leftJoin(t_favouriteGame).on(t_game.id.eq(t_favouriteGame.game.id))
                .where(getWhere())
                .fetchCount();
    }

    @Override
    public CallbackDataProvider.FetchCallback<GameDTO, Void> getFetchCallback() {
        return query -> {
            List<QuerySortOrder> sortOrders = (query.getSortOrders().isEmpty()) ? QuerySortOrder.asc("name").build() : query.getSortOrders();
            OrderSpecifier<?>[] orderList = Stream.of(sortOrders)
                    .flatMap(Collection::stream)
                    .flatMap(this::getOrderSpecifiers)
                    .toArray(OrderSpecifier<?>[]::new);

            return queryFactory.select(gameGameDtoProjection)
                    .from(t_game)
                    .leftJoin(t_favouriteGame).on(t_game.id.eq(t_favouriteGame.game.id))
                    .where(getWhere())
                    .orderBy(orderList)
                    .limit(query.getLimit())
                    .offset(query.getOffset())
                    .fetch()
                    .stream();
        };
    }

    @Override
    public Stream<String> fetchRatings() {
        return queryFactory.selectDistinct(RATING_EXPRESSION)
                .from(t_game)
                .fetch()
                .stream();
    }

    @Override
    public Stream<String> fetchDevelopers() {
        return queryFactory.selectDistinct(DEVELOPER_EXPRESSION)
                .from(t_game)
                .fetch()
                .stream();
    }

    @Override
    public Stream<String> fetchPublishers() {
        return queryFactory.selectDistinct(PUBLISHER_EXPRESSION)
                .from(t_game)
                .fetch()
                .stream();
    }

    private Predicate[] getWhere() {
        ArrayList<Predicate> where = new ArrayList<>();

        Optional.ofNullable(filter.getLike())
                .filter(someSubstring -> !ObjectUtils.isEmpty(someSubstring))
                .map(like -> t_game.name.containsIgnoreCase(like)
                        .or(t_game.description1.containsIgnoreCase(like))
                        .or(t_game.description2.containsIgnoreCase(like)))
                .ifPresent(where::add);
        Optional.ofNullable(filter.getFavourite())
                .filter(someCollection -> !someCollection.isEmpty())
                .map(FAVOURITE_EXPRESSION::in)
                .ifPresent(where::add);
        Optional.ofNullable(filter.getLikeName())
                .filter(someSubstring -> !ObjectUtils.isEmpty(someSubstring))
                .map(t_game.name::containsIgnoreCase)
                .ifPresent(where::add);
        Optional.ofNullable(filter.getReleaseDateFrom())
                .map(t_game.releaseDate::after)
                .ifPresent(where::add);
        Optional.ofNullable(filter.getReleaseDateTo())
                .map(t_game.releaseDate::before)
                .ifPresent(where::add);
        Optional.ofNullable(filter.getRatingIds())
                .filter(someCollection -> !someCollection.isEmpty())
                .map(RATING_EXPRESSION::in)
                .ifPresent(where::add);
        Optional.ofNullable(filter.getPriceFrom())
                .map(t_game.price::goe)
                .ifPresent(where::add);
        Optional.ofNullable(filter.getPriceTo())
                .map(t_game.price::loe)
                .ifPresent(where::add);
        Optional.ofNullable(filter.getDeveloperIds())
                .filter(someCollection -> !someCollection.isEmpty())
                .map(DEVELOPER_EXPRESSION::in)
                .ifPresent(where::add);
        Optional.ofNullable(filter.getPublisherIds())
                .filter(someCollection -> !someCollection.isEmpty())
                .map(PUBLISHER_EXPRESSION::in)
                .ifPresent(where::add);
        Optional.ofNullable(filter.getPicAvailable())
                .filter(someCollection -> !someCollection.isEmpty())
                .map(PIC_AVAILABLE_EXPRESSION::in)
                .ifPresent(where::add);
        Optional.ofNullable(filter.getAvailable())
                .filter(someCollection -> !someCollection.isEmpty())
                .map(t_game.availability::in)
                .ifPresent(where::add);

        return where.toArray(new Predicate[0]);
    }

    private Stream<? extends OrderSpecifier<?>> getOrderSpecifiers(QuerySortOrder item) {
        String sorted = item.getSorted();
        if (GameDTO.Fields.name.toString().equals(sorted))
            return getOrderSpecifierStream(t_game.name, item.getDirection());
        else if (GameDTO.Fields.releaseDate.toString().equals(sorted))
            return getOrderSpecifierStream(t_game.releaseDate, item.getDirection());
        else if (GameDTO.Fields.rating.toString().equals(sorted))
            return getOrderSpecifierStream(t_game.rating, item.getDirection());
        else if (GameDTO.Fields.price.toString().equals(sorted))
            return getOrderSpecifierStream(t_game.price, item.getDirection());
        else if (GameDTO.Fields.developer.toString().equals(sorted))
            return getOrderSpecifierStream(t_game.developer, item.getDirection());
        else if (GameDTO.Fields.publisher.toString().equals(sorted))
            return getOrderSpecifierStream(t_game.publisher, item.getDirection());
        else if ("available".equals(sorted))
            return getOrderSpecifierStream(t_game.availability, item.getDirection());
        else if ("favourite".equals(sorted)) {
            return getOrderSpecifierStream(FAVOURITE_EXPRESSION, item.getDirection());
        }
        /* custom */
        else if ("pic".equals(sorted)) {
            BooleanExpression picExpression = new CaseBuilder().when(t_game.picUrl.isNull()).then(false).otherwise(true);
            return getOrderSpecifierStream(picExpression, item.getDirection());
        }
        return Stream.empty();
    }

    private <T extends ComparableExpressionBase<?>> Stream<OrderSpecifier<?>> getOrderSpecifierStream(T path, SortDirection direction) {
        return SortDirection.DESCENDING.equals(direction) ? Stream.of(path.desc().nullsLast()) : Stream.of(path.asc().nullsFirst());
    }

}
