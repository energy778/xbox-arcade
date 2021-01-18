create table if not exists favourite
(
    id      serial  not null constraint favourite_pk primary key,
    id_game uuid    not null
        constraint favourite_game_id_fk
            references game
            on update cascade on delete cascade
);

comment on table favourite is 'избранное';

