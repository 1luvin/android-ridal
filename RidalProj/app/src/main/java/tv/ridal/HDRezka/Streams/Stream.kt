package tv.ridal.HDRezka.Streams

data class Stream(val quality: String, val url: String)
{
    class FilmTranslator() : FilmStreamData()
    {
        lateinit var name: String
        constructor(name: String, id: String, translatorId: String, camrip: String, ads: String, director: String) : this()
        {
            this.name = name
            this.id = id
            this.translatorId = translatorId
            this.camrip = camrip
            this.ads = ads
            this.director = director
        }
    }

    class SeriesTranslator
    {
        lateinit var name: String
        lateinit var translatorId: String
    }

    class Season
    {
        lateinit var title: String
        lateinit var id: String
    }

}