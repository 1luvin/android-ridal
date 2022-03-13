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
}