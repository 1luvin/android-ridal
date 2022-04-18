package tv.ridal.hdrezka

import tv.ridal.R
import tv.ridal.util.Locale

class HDRezka
{
    companion object
    {
        const val PAGE_CAPACITY = 36

        const val FILM = "Фильм"
        const val SERIAL = "Сериал"
        const val CARTOON = "Мультфильм"
        const val ANIME = "Аниме"

        val sectionNames: Array<String> = arrayOf(
            Locale.string(R.string.films),
            Locale.string(R.string.series),
            Locale.string(R.string.cartoons),
            Locale.string(R.string.anime)
        )

        const val RATINGS = "Рейтинги"
        const val IMDB = "IMDb"
        const val KP = "Кинопоиск"
        const val IN_LISTS = "Входит в списки"
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

        val section_urls: Array<String> = arrayOf(
            URL_FILMS,
            URL_SERIES,
            URL_CARTOONS,
            URL_ANIME
        )

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

        fun createUrl(section: String = "", genre: String = "", sorting: String = "") : String
        {
            var url = url_base + section + genre
            if (sorting != "")
                url += sorting_base + sorting

            return url
        }


        fun getSectionNameByMovieType(type: String) : String
        {
            return when (type)
            {
                FILM -> Locale.string(R.string.films)
                SERIAL -> Locale.string(R.string.series)
                CARTOON -> Locale.string(R.string.cartoons)
                ANIME -> Locale.string(R.string.anime)

                else -> "error_type"
            }
        }
    }

    enum class Filters
    {
        NO_FILTERS, SECTION_SORTING, GENRE_SORTING, SORTING
    }
}





































//