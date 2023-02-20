package tv.ridal.hdrezka

import tv.ridal.R
import tv.ridal.util.Locale

class HDRezka {

    companion object {

        const val PAGE_CAPACITY = 36

        const val FILM = "Фильм"
        const val SERIAL = "Сериал"
        const val CARTOON = "Мультфильм"
        const val ANIME = "Аниме"

        const val RATINGS = "Рейтинги"
        const val IMDB = "IMDb"
        const val KP = "Кинопоиск"
        const val IN_LISTS = "Входит в списки"
        const val RELEASE_DATE = "Дата выхода"
        const val COUNTRY = "Страна"
        const val PRODUCER = "Режиссер"
        const val GENRE = "Жанр"
        const val TIME = "Время"
        const val FROM_SERIES = "Из серии"
        const val ACTORS = "В ролях актеры"

        const val URL_BASE = "http://hdrezka2p2erm.net/"

        val URL_FILMS
            get() = URL_BASE + "films/"
        val URL_SERIES
            get() = URL_BASE + "series/"
        val URL_CARTOONS
            get() = URL_BASE + "cartoons/"
        val URL_ANIME
            get() = URL_BASE + "animation/"


        val movieSections: List<MovieSection> = listOf(
            MovieSection(Locale.string(R.string.films), URL_FILMS),
            MovieSection(Locale.string(R.string.series), URL_SERIES),
            MovieSection(Locale.string(R.string.cartoons), URL_CARTOONS),
            MovieSection(Locale.string(R.string.anime), URL_ANIME),
        )
        val sectionNames: List<String> = movieSections.map { it.name }
        val sectionUrls: List<String> = movieSections.map { it.url }


        fun createUrl(
            baseUrl: String = URL_BASE,
            sectionUrl: String? = null,
            genreUrl: String? = null,
            sortingUrl: String? = null
        ): String {
            var url = baseUrl
            url += genreUrl ?: ""
            url += sortingUrl ?: ""
            url += sectionUrl ?: ""

            return url
        }

        fun getSectionNameByMovieType(type: String): String {
            return when (type) {
                FILM -> Locale.string(R.string.films)
                SERIAL -> Locale.string(R.string.series)
                CARTOON -> Locale.string(R.string.cartoons)
                ANIME -> Locale.string(R.string.anime)

                else -> "error_type"
            }
        }

        fun getSectionUrlByMovieType(type: String): String {
            return when (type) {
                FILM -> URL_FILMS
                SERIAL -> URL_SERIES
                CARTOON -> URL_CARTOONS
                ANIME -> URL_ANIME

                else -> "error_type"
            }
        }
    }

    data class MovieSection(val name: String, val url: String)
}