package ru.veretennikov.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.veretennikov.domain.Game;

import java.text.MessageFormat;

public class GameSpecification {

    private GameSpecification() {}

    public static Specification<Game> getEmpty() {
//        Specification<Game> newSpec = Specification.where(null);
        return (root, query, builder) -> builder.and();
    }

    public static Specification<Game> quickSearch(String expression) {
        return likeName(expression)
                .or(likeDescription1(expression))
                .or(likeDescription2(expression));
    }

    public static Specification<Game> likeName(String expression) {
        return (root, query, builder) -> builder.like(builder.lower(builder.concat("", root.get("name"))), contains(expression.toLowerCase()));
    }

    public static Specification<Game> likeDescription1(String expression) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("description1")), contains(expression.toLowerCase()));
    }

    public static Specification<Game> likeDescription2(String expression) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("description2")), contains(expression.toLowerCase()));
//      or: return (root, query, builder) -> builder.like(builder.lower(builder.concat("", root.get("description2"))), contains(expression.toLowerCase()));
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression);
    }

}
