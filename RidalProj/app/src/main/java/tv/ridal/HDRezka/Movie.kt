package tv.ridal.HDRezka

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(val name: String, val posterUrl: String, val type: Type, val url: String, var info: Info? = null) : Parcelable
{
    @Parcelize
    data class Type(val ruType: String, val isSerial: Boolean) : Parcelable

    @Parcelize
    class Info : Parcelable
    {
        var hdPosterUrl: String? = null

        // Рейтинги
        var ratings: ArrayList<Rating>? = null
        // Входит в списки
        var inLists: ArrayList<List>? = null
        // Слоган
        var slogan: String? = null
        // Дата выхода
        var releaseDate: ReleaseDate? = null
        // Страны
        var countries: ArrayList<Country>? = null
        // Режиссеры
        var producers: ArrayList<Person>? = null
        // Жанры
        var genres: ArrayList<Genre>? = null
        // В переводе
        var inTranslations: String? = null
        // Длительность
        var duration: String? = null
        // Из коллекций
        var inCollections: ArrayList<List>? = null
        // В ролях актеры
        var actors: ArrayList<Person>? = null

        var description: Description? = null

    }

    @Parcelize
    class Rating : Parcelable
    {
        var whose: String? = null
        var value: Float? = null
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