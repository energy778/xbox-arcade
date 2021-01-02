create table if not exists game
(
    id           uuid         not null
        constraint game_pk
            primary key,
    name      varchar(150) not null,
    game_url     varchar,
    pic_url      varchar,
    release_date date,
    description1 varchar,
    description2 varchar,
    rating       varchar(100),
    price        integer,
    location     varchar,
    availability boolean not null,
    date_issue   date,
    developer    varchar(100),
    publisher    varchar(100)
);

comment on table game is 'справочник игр';

create table if not exists game_genre
(
    id      serial  not null constraint game_genre_pk primary key,
    id_game uuid    not null
        constraint game_genre_game_id_fk
            references game
            on update cascade on delete cascade,
    name   varchar not null
);

comment on table game_genre is 'жанры игр';

create table if not exists game_screen
(
    id      serial  not null
        constraint game_screen_pk
            primary key,
    id_game uuid    not null
        constraint game_screen_game_id_fk
            references game
            on update cascade on delete cascade,
    name    varchar not null,
    url     varchar not null
);

comment on table game_screen is 'скриншоты игры';
