package ru.veretennikov.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QTuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.veretennikov.domain.QGame;
import ru.veretennikov.dto.GameDTO;

import javax.persistence.EntityManager;

@Service
public class GameCallbackProviderQueryDSL extends GameCallbackProvider {

    private static final QGame t_game = QGame.game;
    private static final QTuple GAME_DTO_SELECT = Projections.tuple(t_game.id,
            t_game.name,
            t_game.gameUrl,
            t_game.picUrl,
            t_game.releaseDate,
            t_game.description1,
            t_game.description2,
            t_game.rating,
            t_game.price,
            t_game.location,
            t_game.availability,
            t_game.dateIssue,
            t_game.developer,
            t_game.publisher);
    private static final MappingProjection<GameDTO> gameGameDtoProjection = new MappingProjection<>(GameDTO.class, GAME_DTO_SELECT.getArgs().toArray(new Expression<?>[0])) {
        @Override
        protected GameDTO map(Tuple gameTuple) {
            return GameDTO.builder()
                        .id(gameTuple.get(t_game.id))
                        .name(gameTuple.get(t_game.name))
                        .gameUrl(gameTuple.get(t_game.gameUrl))
                        .picUrl(gameTuple.get(t_game.picUrl))
                        .releaseDate(gameTuple.get(t_game.releaseDate))
                        .description1(gameTuple.get(t_game.description1))
                        .description2(gameTuple.get(t_game.description2))
                        .rating(gameTuple.get(t_game.rating))
                        .price(gameTuple.get(t_game.price))
                        .location(gameTuple.get(t_game.location))
                        .availability(gameTuple.get(t_game.availability))
                        .dateIssue(gameTuple.get(t_game.dateIssue))
                        .developer(gameTuple.get(t_game.developer))
                        .publisher(gameTuple.get(t_game.publisher))
                        .build();
        }
    };

    private final JPAQueryFactory queryFactory;

    public GameCallbackProviderQueryDSL(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public CallbackDataProvider.CountCallback<GameDTO, Void> getCountCallback() {
        return query -> {
            if (ObjectUtils.isEmpty(like))
                return (int) queryFactory.from(t_game).fetchCount();
            else
                return (int) queryFactory.from(t_game)
                    .where(getWhere())
                    .fetchCount();
        };
    }

    @Override
    public CallbackDataProvider.FetchCallback<GameDTO, Void> getFetchCallback() {
        return query -> {
            if (ObjectUtils.isEmpty(like))
                return queryFactory.select(gameGameDtoProjection)
                        .from(t_game)
                        .limit(query.getLimit())
                        .offset(query.getOffset())
                        .fetch()
                        .stream();
            else
                return queryFactory.select(gameGameDtoProjection)
                        .from(t_game)
                        .where(getWhere())
                        .limit(query.getLimit())
                        .offset(query.getOffset())
                        .fetch()
                        .stream();
        };
    }

    private BooleanExpression getWhere() {
        return t_game.name.containsIgnoreCase(like)
                .or(t_game.description1.containsIgnoreCase(like))
                .or(t_game.description2.containsIgnoreCase(like));
    }

}
