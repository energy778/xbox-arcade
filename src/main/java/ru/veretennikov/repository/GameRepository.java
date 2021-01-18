package ru.veretennikov.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veretennikov.domain.Game;
import ru.veretennikov.dto.GameDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID>, JpaSpecificationExecutor<Game> {

    @Query("select new ru.veretennikov.dto.GameDTO(g.id, g.name, g.picUrl, g.releaseDate, g.rating, g.price, g.availability, g.developer, g.publisher, case when f.id is null then false else true end ) " +
            "from Game g " +
            "left join fetch FavouriteGame f on g.id = f.game.id " +
            "where g.id = :uuid")
    Optional<GameDTO> findGameById(UUID uuid);

    @Query("select new ru.veretennikov.dto.GameDTO(g.id, g.name, g.picUrl, g.releaseDate, g.rating, g.price, g.availability, g.developer, g.publisher, case when f.id is null then false else true end ) " +
            "from Game g " +
            "left join fetch FavouriteGame f on g.id = f.game.id")
    List<GameDTO> findAllGames();

    @Query("select new ru.veretennikov.dto.GameDTO(g.id, g.name, g.picUrl, g.releaseDate, g.rating, g.price, g.availability, g.developer, g.publisher, case when f.id is null then false else true end ) " +
            "from Game g " +
            "left join fetch FavouriteGame f on g.id = f.game.id")
    List<GameDTO> findAllGames(Pageable offsetBasedPageRequest);

//    https://stackoverflow.com/questions/26379522/can-i-combine-a-query-definition-with-a-specification-in-a-spring-data-jpa-repo - problem doesn't decisioned
    @Query("select new ru.veretennikov.dto.GameDTO(g.id, g.name, g.picUrl, g.releaseDate, g.rating, g.price, g.availability, g.developer, g.publisher, case when f.id is null then false else true end ) " +
            "from Game g " +
            "left join fetch FavouriteGame f on g.id = f.game.id")
    Page<GameDTO> findAllGames(Specification<Game> specification, Pageable offsetBasedPageRequest);

    @Query("select new ru.veretennikov.dto.GameDTO(g.id, g.name, g.picUrl, g.releaseDate, g.rating, g.price, g.availability, g.developer, g.publisher, case when f.id is null then false else true end ) " +
            "from Game g " +
            "left join fetch FavouriteGame f on g.id = f.game.id " +
            "WHERE lower(g.name) like lower(concat('%', :name, '%')) " +
            "or lower(g.description1) like lower(concat('%', :name, '%')) " +
            "or lower(g.description2) like lower(concat('%', :name, '%'))")
    List<GameDTO> findAllByNameLike(@Param("name") String name);

    @Query("select new ru.veretennikov.dto.GameDTO(g.id, g.name, g.picUrl, g.releaseDate, g.rating, g.price, g.availability, g.developer, g.publisher, case when f.id is null then false else true end ) " +
            "from Game g " +
            "left join fetch FavouriteGame f on g.id = f.game.id " +
            "WHERE lower(g.name) like lower(concat('%', :name, '%')) " +
            "or lower(g.description1) like lower(concat('%', :name, '%')) " +
            "or lower(g.description2) like lower(concat('%', :name, '%'))")
    List<GameDTO> findAllByNameLike(@Param("name") String name, Pageable request);

//    https://vladmihalcea.com/hibernate-multiplebagfetchexception/     against cartesian product

    @Query("SELECT g " +
            "FROM Game g " +
            "LEFT JOIN FETCH g.genres " +
            "WHERE g.id = :id")
    Optional<Game> findOneWithGenresById(@Param("id") UUID id);

    @Query("SELECT g " +
            "FROM Game g " +
            "LEFT JOIN FETCH g.screens " +
            "WHERE g = :game")
    Optional<Game> findOneWithAllDetailsByGameWithGenres(@Param("game") Game game);

    @Query("SELECT g " +
            "FROM Game g " +
            "LEFT JOIN FETCH g.genres")
    List<Game> findAllWithGenres();

    @Query("SELECT g " +
            "FROM Game g " +
            "LEFT JOIN FETCH g.screens " +
            "WHERE g in :games")
    List<Game> findAllWithAllDetailsByGamesWithGenres(@Param("games") List<Game> games);

}
