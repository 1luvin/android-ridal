package tv.ridal.Application

class Locale
{
    companion object
    {
        const val RU: Int = 0

        fun setLocale(locale: Int)
        {
            when (locale)
            {
                RU ->
                {
                    texts[text_sett] = "Настройки"
                    texts[text_visualAppearance] = "Внешний вид"
                    texts[text_theme] = "Тема"
                    texts[text_theme_asInSystem] = "Как в системе"
                    texts[text_theme_light] = "Светлая"
                    texts[text_theme_dark] = "Темная"
                    texts[text_done] = "Готово"

                    texts[text_catalog] = "Каталог"
                    texts[text_collections] = "Подборки"
                    texts[text_search] = "Поиск"

                    texts[hint_search] = "Фильм, сериал, мультфильм, аниме"

                    texts[share_application] = "Привет, я использую ${ApplicationLoader.APP_NAME} для просмотра кино. " +
                            "Присоеденяйся! Скачать его можно здесь: ${ApplicationLoader.WEBSITE}"

                    texts[text_section] = "Раздел"
                    texts[text_films] = "Фильмы"
                    texts[text_series] = "Сериалы"
                    texts[text_cartoons] = "Мультфильмы"
                    texts[text_anime] = "Аниме"

                    texts[text_favourites] = "Избранное"
                    texts[text_empty] = "Пусто"

                    texts[text_newFolder] = "Новая папка..."
                    texts[text_folderName] = "Название папки"
                    texts[text_renameTo] = "Переименовать в..."
                    texts[text_newFolderName] = "Новое название папки"

                    texts[text_filters] = "Фильтры"

                    texts[text_watch] = "Смотреть"

                    texts[text_inLists] = "Входит в списки"
                    texts[text_inCollections] = "Входит в коллекции"
                    texts[text_country] = "Страна"
                    texts[text_actors] = "Актеры"
                    texts[text_producers] = "Режиссеры"

                    texts[text_translation] = "Озвучка"
                    texts[text_season] = "Сезон"
                    texts[text_episode] = "Серия"

                    /*
                        Жанры
                     */

                    texts[text_genre] = "Жанр"
                    texts[text_allGenres] = "Все жанры"

                    // Фильмы
                    texts[genre_western] = "Вестерны"
                    texts[genre_arthouse] = "Арт-хаус"
                    texts[genre_crime] = "Криминал"
                    texts[genre_fiction] = "Фантастика"
                    texts[genre_horror] = "Ужасы"
                    texts[genre_erotic] = "Эротика"
                    texts[genre_theatre] = "Театр"
                    texts[genre_our] = "Наши"
                    texts[genre_family] = "Семейные"
                    texts[genre_action] = "Боевики"
                    texts[genre_adventures] = "Приключения"
                    texts[genre_comedy] = "Комедии"
                    texts[genre_musical] = "Мюзиклы"
                    texts[genre_kids] = "Детские"
                    texts[genre_concert] = "Концерт"
                    texts[genre_ukrainian] = "Украинские"
                    texts[genre_fantasy] = "Фэнтези"
                    texts[genre_military] = "Военные"
                    texts[genre_drama] = "Драмы"
                    texts[genre_melodrama] = "Мелодрамы"
                    texts[genre_historical] = "Исторические"
                    texts[genre_travel] = "Путешествия"
                    texts[genre_standup] = "Стендап"
                    texts[genre_foreign] = "Зарубежные"
                    texts[genre_biographical] = "Биографические"
                    texts[genre_detective] = "Детективы"
                    texts[genre_sport] = "Спортивные"
                    texts[genre_thriller] = "Триллеры"
                    texts[genre_documentary] = "Документальные"
                    texts[genre_cognitive] = "Познавательные"
                    texts[genre_short] = "Короткометражные"

                    // Сериалы
                    texts[genre_realtv] = "Реальное ТВ"
                    texts[genre_russian] = "Русские"
                    texts[genre_telecasts] = "Телепередачи"
                    texts[genre_musical2] = "Музыкальные"

                    // Мультфильмы
                    texts[genre_multseries] = "Мультсериалы"
                    texts[genre_fairytale] = "Сказки"
                    texts[genre_adult] = "Для взрослых"
                    //texts[genre_animation] = "Аниме"
                    texts[genre_fullLength] = "Полнометражные"
                    texts[genre_soyzmyltfilm] = "Советские"

                    // Аниме
                    texts[genre_romance] = "Романтические"
                    texts[genre_samurai] = "Самурайский боевик"
                    texts[genre_parody] = "Пародия"
                    texts[genre_kodomo] = "Кодомо"
                    texts[genre_shounenai] = "Сёнэн-ай"
                    texts[genre_school] = "Школа"
                    texts[genre_shoujoai] = "Сёдзё-ай"
                    texts[genre_ecchi] = "Этти"
                    texts[genre_shoujo] = "Сёдзё"
                    texts[genre_mahoushoujo] = "Махо-сёдзё"
                    texts[genre_mystery] = "Мистические"
                    texts[genre_fighting] = "Боевые искусства"
                    texts[genre_everyday] = "Повседневность"
                    texts[genre_shounen] = "Сёнэн"
                    texts[genre_mecha] = "Меха"

                    /*
                        Сортировка
                     */

                    texts[text_sorting] = "Сортировка"

                    texts[sorting_last] = "Последние поступления"
                    texts[sorting_popular] = "Популярные"
                    texts[sorting_watching] = "Сейчас смотрят"

                    texts[text_showResults] = "Показать результаты"
                }
            }
        }

        /*
            Ключи для строк
         */
        
        const val text_sett = "text_sett"
        const val text_visualAppearance = "text_visualAppearance"
        const val text_theme = "text_theme"
        const val text_theme_asInSystem = "text_theme_asInSystem"
        const val text_theme_light = "text_theme_light"
        const val text_theme_dark = "text_theme_dark"
        const val text_done = "text_done"

        const val text_catalog = "text_catalog"
        const val text_collections = "text_collections"
        const val text_search = "text_search"

        const val hint_search = "hint_search"

        const val share_application = "share_application"

        const val text_section = "text_section"
        const val text_films = "text_films"
        const val text_series = "text_series"
        const val text_cartoons = "text_cartoons"
        const val text_anime = "text_anime"

        const val text_favourites = "text_favourites"
        const val text_empty = "text_empty"

        const val text_newFolder = "text_newFolder"
        const val text_folderName = "text_folderName"
        const val text_renameTo = "text_renameTo"
        const val text_newFolderName = "text_newFolderName"

        const val text_filters = "text_filters"

        const val text_watch = "text_watch"

        const val text_inLists = "text_inLists"
        const val text_inCollections = "text_inCollections"
        const val text_country = "text_country"
        const val text_actors = "text_actors"
        const val text_producers = "text_producers"

        const val text_translation = "text_translation"
        const val text_season = "text_season"
        const val text_episode = "text_episode"

        /*
            Жанры
         */

        const val text_genre = "text_genre"
        const val text_allGenres = "text_allGenres"

        // Фильмы
        const val genre_western = "genre_western"
        const val genre_arthouse = "genre_arthouse"
        const val genre_crime = "genre_crime"
        const val genre_fiction = "genre_fiction"
        const val genre_horror = "genre_horror"
        const val genre_erotic = "genre_erotic"
        const val genre_theatre = "genre_theatre"
        const val genre_our = "genre_our"
        const val genre_family = "genre_family"
        const val genre_action = "genre_action"
        const val genre_adventures = "genre_adventures"
        const val genre_comedy = "genre_comedy"
        const val genre_musical = "genre_musical"
        const val genre_kids = "genre_kids"
        const val genre_concert = "genre_concert"
        const val genre_ukrainian = "genre_ukrainian"
        const val genre_fantasy = "genre_fantasy"
        const val genre_military = "genre_military"
        const val genre_drama = "genre_drama"
        const val genre_melodrama = "genre_melodrama"
        const val genre_historical = "genre_historical"
        const val genre_travel = "genre_travel"
        const val genre_standup = "genre_standup"
        const val genre_foreign = "genre_foreign"
        const val genre_biographical = "genre_biographical"
        const val genre_detective = "genre_detective"
        const val genre_sport = "genre_sport"
        const val genre_thriller = "genre_thriller"
        const val genre_documentary = "genre_documentary"
        const val genre_cognitive = "genre_cognitive"
        const val genre_short = "genre_short"

        // Сериалы
        const val genre_realtv = "genre_realtv"
        const val genre_russian = "genre_russian"
        const val genre_telecasts = "genre_telecasts"
        const val genre_musical2 = "genre_musical2"

        // Мультфильмы
        const val genre_multseries = "genre_multseries"
        const val genre_fairytale = "genre_fairytale"
        const val genre_adult = "genre_adult"
        //const val genre_animation = "genre_animation"
        const val genre_fullLength = "genre_fullLength"
        const val genre_soyzmyltfilm = "genre_soyzmyltfilm"

        // Аниме
        const val genre_romance = "genre_romance"
        const val genre_samurai = "genre_samurai"
        const val genre_parody = "genre_parody"
        const val genre_kodomo = "genre_kodomo"
        const val genre_shounenai = "genre_shounenai"
        const val genre_school = "genre_school"
        const val genre_shoujoai = "genre_shoujoai"
        const val genre_ecchi = "genre_ecchi"
        const val genre_shoujo = "genre_shoujo"
        const val genre_mahoushoujo = "genre_mahoushoujo"
        const val genre_mystery = "genre_mystery"
        const val genre_fighting = "genre_fighting"
        const val genre_everyday = "genre_everyday"
        const val genre_shounen = "genre_shounen"
        const val genre_mecha = "genre_mecha"

        /*
            Сортировка
         */

        const val text_sorting = "text_sorting"

        const val sorting_last = "sorting_last"
        const val sorting_popular = "sorting_popular"
        const val sorting_watching = "sorting_watching"

        const val text_showResults = "text_showResults"


        private var texts = HashMap<String, String>()

        fun text(textKey: String) : String
        {
            return texts[textKey] ?: "bad_text_key"
        }
    }
}


































//