package tv.ridal.hdrezka


data class Movie(val name: String, val url: String)
{
    var posterUrl: String? = null
    var type: Movie.Type? = null
    var rating: String? = null

    data class Type(val ruType: String, val isSerial: Boolean)

    class Info
    {
        // Постер
        var hdPosterUrl: String? = null
        // Рейтинги
        var ratings: ArrayList<Rating>? = null
        fun hasRatings() = ratings != null
        // Входит в списки
        var inLists: ArrayList<NameUrl>? = null
        fun hasInLists() = inLists != null
        // Дата выхода
        var releaseYear: String? = null
        fun hasReleaseYear() = releaseYear != null
        // Страны
        var countries: ArrayList<NameUrl>? = null
        fun hasCountries() = countries != null
        // Режиссеры
        var producers: ArrayList<NameUrl>? = null
        fun hasProducers() = producers != null
        // Жанры
        var genres: ArrayList<NameUrl>? = null
        fun hasGenres() = genres != null
        // В переводе
        var inTranslations: String? = null
        fun hasInTranslations() = inTranslations != null
        // Длительность
        var duration: String? = null
        fun hasDuration() = duration != null
        // Из коллекций
        var inCollections: ArrayList<NameUrl>? = null
        fun hasInCollections() = inCollections != null
        // В ролях актеры
        var actors: ArrayList<NameUrl>? = null
        fun hasActors() = actors != null
        // Описание
        var description: Description? = null
        fun hasDescription() = description != null

    }

    data class NameUrl(val name: String, val url: String)

    data class Rating(val whose: String, val value: String)

    data class Description(val title: String, val text: String)

}


































//