package tv.ridal.hdrezka


data class Movie(val name: String, val url: String) {

    var posterUrl: String? = null
    var type: String? = null
    var rating: String? = null

    class Info {

        // Poster
        var hdPosterUrl: String? = null

        // Ratings
        var ratings: ArrayList<Rating>? = null
        fun hasRatings() = ratings != null

        // In lists
        var inLists: ArrayList<NameUrl>? = null
        fun hasInLists() = inLists != null

        // Release year
        var releaseYear: String? = null

        // Country
        var countries: ArrayList<NameUrl>? = null
        fun hasCountries() = countries != null

        // Producers
        var producers: ArrayList<NameUrl>? = null
        fun hasProducers() = producers != null

        // Genre
        var genres: ArrayList<NameUrl>? = null
        fun hasGenres() = genres != null

        // Duration
        var duration: String? = null

        // In collections
        var inCollections: ArrayList<NameUrl>? = null
        fun hasInCollections() = inCollections != null

        // Actors
        var actors: ArrayList<NameUrl>? = null
        fun hasActors() = actors != null

        // Description
        lateinit var description: String
    }

    data class NameUrl(val name: String, val url: String)

    data class Rating(val whose: String, val value: String)
}