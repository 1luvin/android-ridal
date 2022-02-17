package tv.ridal.HDRezka


data class Movie(val name: String, val posterUrl: String, val type: Type, val url: String)
{
    data class Type(val ruType: String, val isSerial: Boolean)

    class Info
    {
        // Постер
        var hdPosterUrl: String? = null
        // Рейтинги
        var ratings: ArrayList<Rating>? = null
        fun hasRatings() = ratings != null
        // Входит в списки
        var inLists: ArrayList<List>? = null
        fun hasInLists() = inLists != null
        // Слоган
        var slogan: String? = null
        fun hasSlogan() = slogan != null
        // Дата выхода
        var releaseDate: ReleaseDate? = null
        fun hasReleaseDate() = releaseDate != null
        // Страны
        var countries: ArrayList<Country>? = null
        fun hasCountries() = countries != null
        // Режиссеры
        var producers: ArrayList<Person>? = null
        fun hasProducers() = producers != null
        // Жанры
        var genres: ArrayList<Genre>? = null
        fun hasGenres() = genres != null
        // В переводе
        var inTranslations: String? = null
        fun hasInTranslations() = inTranslations != null
        // Длительность
        var duration: String? = null
        fun hasDuration() = duration != null
        // Из коллекций
        var inCollections: ArrayList<List>? = null
        fun hasInCollections() = inCollections != null
        // В ролях актеры
        var actors: ArrayList<Person>? = null
        fun hasActors() = actors != null
        // Описание
        var description: Description? = null
        fun hasDescription() = description != null

    }

    class Rating
    {
        var whose: String? = null
        var value: String? = null
    }

    class List
    {
        var name: String? = null
        var url: String? = null
    }

    class ReleaseDate
    {
        lateinit var date: String
        var yearStartIndex: Int = 0
        lateinit var yearUrl: String
    }

    class Country
    {
        var name: String? = null
        var url: String? = null
    }

    class Person
    {
        var name: String? = null
        var url: String? = null
    }

    class Genre
    {
        var name: String? = null
        var url: String? = null
    }

    class Description
    {
        var title: String? = null
        var text: String? = null
    }

}


































//