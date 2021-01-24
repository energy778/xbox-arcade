create table if not exists review
(
    id      serial  not null constraint review_pk primary key,
    id_game uuid    not null
        constraint review_game_id_fk
            references game
            on update cascade on delete cascade,
    updated   date,
    note varchar
);

comment on table review is 'отзывы';

