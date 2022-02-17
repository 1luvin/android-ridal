package tv.ridal.HDRezka

import tv.ridal.Application.Locale

class HDRezka
{
    companion object
    {

        const val PAGE_CAPACITY = 36

        const val FILM = "Фильм"
        const val SERIAL = "Сериал"
        const val CARTOON = "Мультфильм"
        const val ANIME = "Аниме"

        val SECTION_NAMES
            get() = listOf(
                Locale.text(Locale.text_films),
                Locale.text(Locale.text_series),
                Locale.text(Locale.text_cartoons),
                Locale.text(Locale.text_anime)
            )

        const val RATINGS = "Рейтинги"
        const val IMDB = "IMDb"
        const val KP = "Кинопоиск"

        const val IN_LISTS = "Входит в спписки"
        const val SLOGAN = "Слоган"
        const val RELEASE_DATE = "Дата выхода"
        const val COUNTRY = "Страна"
        const val PRODUCER = "Режиссер"
        const val GENRE = "Жанр"
        const val IN_TRANSLATION = "В переводе"
        const val TIME = "Время"
        const val FROM_SERIES = "Из серии"
        const val ACTORS = "В ролях актеры"

        var url_base = "https://rezka.ag/"

        val URL_FILMS
            get() = url_base + "films/"
        val URL_SERIES
            get() = url_base + "series/"
        val URL_CARTOONS
            get() = url_base + "cartoons/"
        val URL_ANIME
            get() = url_base + "animation/"

        val SECTION_URLS
            get() = listOf(URL_FILMS, URL_SERIES, URL_CARTOONS, URL_ANIME)


        // Секции
        const val url_films = "films/"
        const val url_series = "series/"
        const val url_cartoons = "cartoons/"
        const val url_anime = "animation/"

        // Фильтры
        const val sorting_base = "?filter="
        const val sorting_last = "last"
        const val sorting_popular = "popular"
        const val sorting_watching = "watching"

        fun createUrl(section: String, genre: String = "", sorting: String = "") : String
        {
            var url = url_base + section + genre
            if (sorting != "")
                url += sorting_base + sorting

            return url
        }

    }
}





































//