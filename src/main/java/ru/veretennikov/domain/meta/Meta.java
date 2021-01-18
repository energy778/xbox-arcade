package ru.veretennikov.domain.meta;

public final class Meta {

    public static final String schema = "public";

    private Meta() {
    }

    /**
     * Справочник игр
     */
    public static final class game {
        public static final String name = "game";

        //region Атрибуты
        public static final class fields {
            /**
             * Идентификатор записи
             */
            public static final String id = "id";
            /**
             * Наименование игры
             */
            public static final String name = "name";
            /**
             * Ссылка на игру
             */
            public static final String game_url = "game_url";
            /**
             * Ссылка на пиктограмму игры
             */
            public static final String pic_url = "pic_url";
            /**
             * Дата выпуска
             */
            public static final String release_date = "release_date";
            /**
             * Описание 1
             */
            public static final String description1 = "description1";
            /**
             * Описание 2
             */
            public static final String description2 = "description2";
            /**
             * Возрастной рейтинг
             */
            public static final String rating = "rating";
            /**
             * Цена
             */
            public static final String price = "price";
            /**
             * Путь к игре
             */
            public static final String location = "location";
            /**
             * Наличие
             */
            public static final String availability = "availability";
            /**
             * Дата выхода
             */
            public static final String date_issue = "date_issue";
            /**
             * Разработчик
             */
            public static final String developer = "developer";
            /**
             * Издатель
             */
            public static final String publisher = "publisher";
            /**
             * Список жанров
             */
            public static final String game_genres = "game_genres";
            /**
             * Список скриншотов
             */
            public static final String game_screens = "game_screens";
        }
        //endregion
    }

    /**
     * Таблица игр по жанрам
     */
    public static final class game_genre {
        public static final String name = "game_genre";

        //region Атрибуты
        public static final class fields {
            /**
             * Идентификатор записи
             */
            public static final String id = "id";
            /**
             * Наименование жанра
             */
            public static final String name = "name";
            /**
             * ID игры
             */
            public static final String id_game = "id_game";
            /**
             * Игра
             */
            public static final String game = "game";
        }
        //endregion
    }

    /**
     * Таблица скриншотов игр
     */
    public static final class game_screen {
        public static final String name = "game_screen";

        //region Атрибуты
        public static final class fields {
            /**
             * Идентификатор записи
             */
            public static final String id = "id";
            /**
             * Имя скриншота
             */
            public static final String name = "name";
            /**
             * Ссылка на скриншот
             */
            public static final String url = "url";
            /**
             * ID игры
             */
            public static final String id_game = "id_game";
            /**
             * Игра
             */
            public static final String game = "game";
        }
        //endregion
    }

    /**
     * Таблица избранного
     */
    public static final class favourite {
        public static final String name = "favourite";

        //region Атрибуты
        public static final class fields {
            /**
             * Идентификатор записи
             */
            public static final String id = "id";
            /**
             * ID игры
             */
            public static final String id_game = "id_game";
            /**
             * Игра
             */
            public static final String game = "game";
        }
        //endregion
    }

}
