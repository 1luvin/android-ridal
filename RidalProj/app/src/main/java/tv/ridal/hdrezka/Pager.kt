package tv.ridal.hdrezka

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Pager
{
    companion object
    {
        fun isNextPageExist(doc: Document) : Boolean
        {
            val navigation = doc.getElementsByClass("b-navigation")
            if (navigation.size > 0) {
                val nav = navigation[0]
                val spans = nav.getElementsByTag("span")
                return spans[spans.size - 2].attr("class") != "no-page"
            }
            return false
        }
        fun isNextPageExist(html: String) : Boolean
        {
            return isNextPageExist( Jsoup.parse(html) )
        }

        fun nextPageUrl(document: Document) : String
        {
            val nav = document.getElementsByClass("b-navigation")[0];
            val aTags = nav.getElementsByTag("a")
            return aTags[aTags.size-1].attr("href")
        }
    }
}