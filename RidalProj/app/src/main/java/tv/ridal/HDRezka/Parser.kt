package tv.ridal.HDRezka

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import tv.ridal.HDRezka.Streams.Stream
import tv.ridal.HDRezka.Streams.StreamData

class Parser
{

    companion object
    {

        /*
            Фильмы, сериалы, мультфильмы, аниме
         */

        fun parseMovies(doc: Document, size: Int = HDRezka.PAGE_CAPACITY): ArrayList<Movie>?
        {
            val movieCardsBox = doc.getElementsByClass("b-content__inline_items")
            if (movieCardsBox.size == 0) return null
            val movieCards = movieCardsBox[0].getElementsByClass("b-content__inline_item")
            if (movieCards.size == 0) return null

            val movies = ArrayList<Movie>()
            movies.ensureCapacity(size)

            for (i in 0 until Math.min(movieCards.size, size))
            {
                val movieCard = movieCards[i]

                val cardCover = movieCard.getElementsByClass("b-content__inline_item-cover")[0]
                val cardLink = movieCard.getElementsByClass("b-content__inline_item-link")[0]
                // название
                val name = cardLink.getElementsByTag("a")[0].text()
                // постер
                val posterUrl = cardCover.getElementsByTag("img")[0].attr("src")
                // тип на русском языке
                var typeText = cardCover.getElementsByClass("entity")[0].text()
                var type: Movie.Type
                /*
                    Типы "Мультфильм" или "Аниме" могут быть односерийные(фильм) или
                    многосерийные
                 */
                if (typeText == HDRezka.FILM) {
                    type = Movie.Type(typeText, false)
                } else if (typeText == HDRezka.SERIAL) {
                    type = Movie.Type(typeText, true)
                } else {
                    val info = cardCover.getElementsByClass("info")
                    if (info.size == 0) {
                        type = Movie.Type(typeText, false)
                    } else {
                        type = Movie.Type(typeText, true)
                    }
                }
                // ссылка
                val url = cardLink.getElementsByTag("a")[0].attr("href")

                val movie = Movie(name, posterUrl, type, url)
                movies.add(movie)
            }

            return movies
        }

        fun parseMovies(html: String, size: Int = HDRezka.PAGE_CAPACITY): ArrayList<Movie>?
        {
            return parseMovies(Jsoup.parse(html), size)
        }


        /*
            Подборки
         */

//        fun parseCollections(doc: Document): ArrayList<Collection>? {
//            val collectionCardsBox = doc.getElementsByClass("b-content__collections_list clearfix")
//            if (collectionCardsBox.size > 0) {
//                val collectionCards =
//                    collectionCardsBox[0].getElementsByClass("b-content__collections_item")
//                if (collectionCards.size > 0) {
//                    val collections = ArrayList<Collection>()
//                    collections.ensureCapacity(36)
//                    for (collectionCard in collectionCards) {
//                        val a = collectionCard.getElementsByTag("a")[0]
//                        // название
//                        val name = a.text()
//                        // постер
//                        val posterURL = collectionCard.getElementsByTag("img")[0].attr("src")
//                        // тип
//                        var type: String = ""
//                        val types =
//                            listOf(HDRezka.FILMS, HDRezka.SERIES, HDRezka.CARTOONS, HDRezka.ANIME)
//                        for (t in types) {
//                            if (name.contains(t, true)) {
//                                type = t
//                                break
//                            }
//                        }
//                        if (type == "") {
//                            if (name == "Как это было (ВОВ)") {
//                                type = HDRezka.SERIES
//                            } else {
//                                type = HDRezka.FILMS
//                            }
//                        }
//                        // ссылка
//                        val url = a.attr("href")
//
//                        val collection = Collection(name, posterURL, type, url)
//                        collections.add(collection)
//                    }
//                    return collections
//                }
//            }
//            return null
//        }

//        fun parseCollections(html: String): ArrayList<Collection>? {
//            return parseCollections(Jsoup.parse(html))
//        }


        /*
            Информация о фильме
         */

        fun parseMovieInfo(doc: Document): Movie.Info
        {
            val mi = Movie.Info()

            mi.hdPosterUrl = doc.select(".b-sidecover > a > img").attr("src")

            val table: Element =
                doc.getElementsByClass("b-post__info")[0].getElementsByTag("tbody")[0]
            val trs: Elements = table.getElementsByTag("tr")

            for (i in 0 until trs.size) {
                val tds: Elements = trs[i].getElementsByTag("td")
                val tdTitle: String = if (tds.size > 1) {
                    tds[0].text().substring(0, tds[0].text().length - 2)
                } else {
                    HDRezka.ACTORS
                }
                val tdData: Element = if (tds.size > 1) {
                    tds[1]
                } else {
                    tds[0]
                }
                when (tdTitle) {
                    HDRezka.RATINGS -> {
                        val spans: Elements = tdData.getElementsByClass("b-post__info_rates")
                        if (spans.size == 0) break

                        mi.ratings = ArrayList()

                        for (j in 0 until spans.size) {
                            val rating = Movie.Rating().apply {
                                whose = spans[j].getElementsByTag("a")[0].text()
                                value = spans[j].getElementsByTag("span")[1].text().toFloat()
                            }
                            mi.ratings!!.add(rating)
                        }
                    }
                    HDRezka.IN_LISTS -> {
                        val aTags = tdData.getElementsByTag("a")
                        if (aTags.size == 0) break

                        mi.inLists = ArrayList()

                        for (aTag in aTags)
                        {
                            val list = Movie.List().apply {
                                name = aTag.text()
                                url = aTag.attr("href")
                            }
                            mi.inLists!!.add(list)
                        }
                    }
                    HDRezka.SLOGAN -> {
                        mi.slogan = tdData.text()
                    }
                    HDRezka.RELEASE_DATE -> {
                        val rDate = Movie.ReleaseDate()
                        rDate.date = tdData.text()
                        val words = rDate.date.split(" ")
                        rDate.yearStartIndex = rDate.date.indexOf(words[2])
                        val a: Element = tdData.getElementsByTag("a")[0]
                        rDate.yearUrl = a.attr("href")

                        mi.releaseDate = rDate
                    }
                    HDRezka.COUNTRY -> {
                        val _as: Elements = tdData.getElementsByTag("a")
                        if (_as.size == 0) break

                        mi.countries = ArrayList()

                        for (a in _as) {
                            val country = Movie.Country().apply {
                                name = a.text()
                                url = a.attr("href")
                            }
                            mi.countries!!.add(country)
                        }
                    }
                    HDRezka.PRODUCER -> {
                        val div: Element = tdData.getElementsByTag("div")[0]
                        val spans: Elements = div.getElementsByClass("item")
                        if (spans.size == 0) break

                        mi.producers = ArrayList()

                        for (j in 0 until spans.size) {
                            val a: Element = spans[j].getElementsByTag("a")[0]
                            val producer = Movie.Person().apply {
                                name = a.text()
                                url = a.attr("href")
                            }
                            mi.producers!!.add(producer)
                        }
                    }
                    HDRezka.GENRE -> {
                        val _as: Elements = tdData.getElementsByTag("a")
                        if (_as.size == 0) break

                        mi.genres = ArrayList()

                        for (a in _as) {
                            val genre = Movie.Genre().apply {
                                name = a.text()
                                url = a.attr("href")
                            }
                            mi.genres!!.add(genre)
                        }
                    }
                    HDRezka.IN_TRANSLATION -> {
                        mi.inTranslations = tdData.text()
                    }
                    HDRezka.TIME -> {
                        mi.duration = tdData.text()
                    }
                    HDRezka.FROM_SERIES -> {
                        val _as: Elements = tdData.getElementsByTag("a")
                        if (_as.size == 0) break

                        mi.inCollections = ArrayList()

                        for (a in _as) {
                            val collection = Movie.List().apply {
                                name = a.text()
                                url = a.attr("href")
                            }
                            mi.inCollections!!.add(collection)
                        }
                    }
                    HDRezka.ACTORS -> {
                        val spans: Elements = tdData.getElementsByClass("item")
                        if (spans.size == 0) break

                        mi.actors = ArrayList()

                        for (span in spans) {
                            val _as: Elements = span.getElementsByTag("a")
                            if (_as.size == 0) continue

                            val a: Element = _as[0]
                            val actor = Movie.Person().apply {
                                name = a.text()
                                url = a.attr("href")
                            }
                            mi.actors!!.add(actor)
                        }
                    }
                }
            }

            mi.description = Movie.Description().apply {
                title = doc.getElementsByClass("b-post__description_title")[0].text()
                text = doc.getElementsByClass("b-post__description_text")[0].text()
            }

            return mi
        }

        fun parseMovieInfo(html: String) : Movie.Info
        {
            return parseMovieInfo(Jsoup.parse(html))
        }


        /*
            Результаты поиска
         */

        fun parseSearchResults(doc: Document): ArrayList<SearchResult>?
        {
            val results = ArrayList<SearchResult>().apply {
                ensureCapacity(5)
            }

            val lis = doc.getElementsByTag("li")
            if (lis.size == 0) return null

            for (li in lis)
            {
                val a = li.select("a")
                val movieName = a.select(".enty").text()

                var movieData: String = ""
                val d = a.textNodes()[0].text()

                val typesList = listOf(
                    HDRezka.CARTOON, HDRezka.FILM, HDRezka.SERIAL, HDRezka.ANIME
                )

                for (i in typesList.indices)
                {
                    if (d.contains(typesList[i], ignoreCase = true)) {
                        movieData += typesList[i]
                        movieData += ", "
                        break
                    }
                    else {
                        if (i == typesList.size - 1)
                        {
                            movieData += HDRezka.FILM
                            movieData += ", "
                        }
                    }
                }

                val years = Regex("[0-9]+").findAll(d)
                    .map(MatchResult::value)
                    .toList()

                for (i in years.indices)
                {
                    if (years[i].length < 4) continue
                    movieData += years[i]
                    if (years.size > 1 && i != years.size - 1)
                    {
                        movieData += "-"
                    }
                }

                if (d.contains(" - ...")) movieData += " - ..."


                val movieRating = a.select(".rating").text()
                val movieUrl = a.attr("href")

                val result = SearchResult(movieName, movieData, movieRating, movieUrl)
                results.add(result)
            }

            return results
        }

        fun parseSearchResults(html: String) : ArrayList<SearchResult>?
        {
            return parseSearchResults(Jsoup.parse(html))
        }


        /*
            Количество единиц кино
         */

        fun parseSectionMoviesSize(doc: Document) : String
        {
            val navigation = doc.getElementsByClass("b-navigation")
            if (navigation.size > 0)
            {
                val nav = navigation[0]
                val aTags = nav.getElementsByTag("a")
                val size = aTags[aTags.size - 2].text().toInt() - 1
                return (HDRezka.PAGE_CAPACITY * size).toString()
            }
            return ""
        }

        fun parseSectionMoviesSize(html: String) : String
        {
            return parseSectionMoviesSize(Jsoup.parse(html))
        }

        /*
            Стримы
         */

        fun parseStreams(html: String) : ArrayList<Stream>
        {
            val streams = ArrayList<Stream>()

            val qualities = arrayOf("360p", "480p", "720p", "1080p", "1080p Ultra", "1440p", "2160p")
            var q_it = 0
            var i = 0
            while (i < html.length - 2) {
                if (html[i] == 'h' && html[i + 1] == 't' && html[i + 2] == 't') {
                    val tmp = StringBuilder()
                    var k = i
                    while (true) {
                        if (html[k] == ' ' || html[k] == ',' || html[k] == '\"') break
                        tmp.append(html[k])
                        k++
                    }
                    i = k
                    var temp = tmp.toString()
                    if ( ! temp.contains("m3u8") && temp.contains("mp4")) {
                        temp = temp.replace("\\\\".toRegex(), "")
                        streams.add( Stream(qualities[q_it], temp) )
                        q_it++
                    }
                }
                i++
            }

            return streams
        }


//        fun parsePersonPhotoUrl(doc: Document): String {
//            val sideCover = doc.getElementsByClass("b-sidecover")
//            if (sideCover.isNotEmpty()) {
//                val img = sideCover[0].getElementsByTag("img")
//                if (img.isNotEmpty()) {
//                    return img[0].attr("src")
//                }
//            }
//            return HDRezka.url(HDRezka.url_noPersonPhoto)
//        }

    }
}



































//