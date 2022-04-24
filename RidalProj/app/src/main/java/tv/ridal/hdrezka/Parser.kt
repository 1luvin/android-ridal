package tv.ridal.hdrezka

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import tv.ridal.hdrezka.Streams.Stream
import tv.ridal.ui.amount
import tv.ridal.ui.msg

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
                // Название
                val name = cardLink.getElementsByTag("a")[0].text()
                // Ссылка
                val url = cardLink.getElementsByTag("a")[0].attr("href")
                // Постер
                val posterUrl = cardCover.getElementsByTag("img")[0].attr("src")
                // Тип, Тип (рейтинг)
                val entity = cardCover.getElementsByClass("entity")[0]

                val iText = entity.ownText().replace(" ", "")
                var type: Movie.Type
                /*
                    Типы "Мультфильм" или "Аниме" могут быть односерийные(фильм) или
                    многосерийные
                 */
                type = if (iText == HDRezka.FILM) {
                    Movie.Type(iText, false)
                } else if (iText == HDRezka.SERIAL) {
                    Movie.Type(iText, true)
                } else {
                    val info = cardCover.getElementsByClass("info")
                    Movie.Type(iText, info.size != 0)
                }

                var rating: String? = null

                val innerI = entity.getElementsByClass("b-category-bestrating rating-grey-string")
                if ( innerI.size != 0 )
                {
                    val text = innerI[0].ownText()
                    if ( text.contains('.') )
                    {
                        rating = text.amount()
                    }
                }

                val movie = Movie(name, url).apply {
                    this.posterUrl = posterUrl
                    this.type = type
                    this.rating = rating
                }
                movies.add(movie)
            }

            return movies
        }

        fun parseMovies(html: String, size: Int = HDRezka.PAGE_CAPACITY): ArrayList<Movie>?
        {
            return parseMovies( Jsoup.parse(html), size )
        }


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
                println(tdTitle)
                when (tdTitle) {
                    HDRezka.RATINGS -> {
                        val spans: Elements = tdData.getElementsByClass("b-post__info_rates")
                        if (spans.size == 0) break

                        mi.ratings = ArrayList()

                        for (j in 0 until spans.size) {
                            val whose = spans[j].getElementsByTag("a")[0].text()
                            val value = spans[j].getElementsByTag("span")[1].text()

                            val rating = Movie.Rating(whose, value)
                            mi.ratings!!.add(rating)
                        }
                    }
                    HDRezka.IN_LISTS -> {
                        val aTags = tdData.getElementsByTag("a")
                        if (aTags.size == 0) break

                        mi.inLists = ArrayList()

                        for (aTag in aTags)
                        {
                            val name = aTag.text()
                            var url = aTag.attr("href")
                            if ( ! url!!.contains("https")) {
                                url = url.replace("http", "https")
                            }

                            val list = Movie.NameUrl(name, url)
                            mi.inLists!!.add(list)
                        }
                    }
                    HDRezka.RELEASE_DATE -> {
                        mi.releaseYear = tdData.text().split(" ")[2]
                    }
                    HDRezka.COUNTRY -> {
                        val _as: Elements = tdData.getElementsByTag("a")
                        if (_as.size == 0) break

                        mi.countries = ArrayList()

                        for (a in _as) {
                            val name = a.text()
                            val url = a.attr("href")

                            val country = Movie.NameUrl(name, url)
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
                            val name = a.text()
                            val url = a.attr("href")

                            val producer = Movie.NameUrl(name, url)
                            mi.producers!!.add(producer)
                        }
                    }
                    HDRezka.GENRE -> {
                        val _as: Elements = tdData.getElementsByTag("a")
                        if (_as.size == 0) break

                        mi.genres = ArrayList()

                        for (a in _as) {
                            val name = a.text()
                            val url = a.attr("href")

                            val genre = Movie.NameUrl(name, url)
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
                            val name = a.text()
                            val url = a.attr("href")

                            val collection = Movie.NameUrl(name, url)
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
                            val name = a.text()
                            val url = a.attr("href")

                            val actor = Movie.NameUrl(name, url)
                            mi.actors!!.add(actor)
                        }
                    }
                }
            }

            val title = doc.getElementsByClass("b-post__description_title")[0].text()
            val text = doc.getElementsByClass("b-post__description_text")[0].text()
            mi.description = Movie.Description(title, text)

            return mi
        }

        fun parseMovieInfo(html: String) : Movie.Info
        {
            return parseMovieInfo( Jsoup.parse(html) )
        }


        /*
            Результаты поиска
         */

        fun parseSearchResults(doc: Document): Pair<ArrayList<SearchResult>, Boolean>?
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

            val hasMore = doc.getElementsByClass("b-search__live_all").size != 0
            msg("${hasMore}")

            return Pair(results, hasMore)
        }

        fun parseSearchResults(html: String) : Pair<ArrayList<SearchResult>, Boolean>?
        {
            return parseSearchResults( Jsoup.parse(html) )
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
            return parseSectionMoviesSize( Jsoup.parse(html) )
        }


        fun parsePersonPhotoUrl(html: String) : String
        {
            val doc = Jsoup.parse(html)
            val sideCover = doc.getElementsByClass("b-sidecover")
            if (sideCover.isNotEmpty()) {
                val img = sideCover[0].getElementsByTag("img")
                if (img.isNotEmpty()) {
                    return img[0].attr("src")
                }
            }
            return "https://static.hdrezka.ac/i/nopersonphoto.png"
        }

    }
}



































//