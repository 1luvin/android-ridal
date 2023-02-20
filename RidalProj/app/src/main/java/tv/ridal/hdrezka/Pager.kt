package tv.ridal.hdrezka

import org.jsoup.nodes.Document

class Pager {

    companion object {

        fun nextPageUrl(document: Document): String? {
            val nav = document.getElementsByClass("b-navigation")
            return if (nav.size > 0) {
                nav[0].getElementsByTag("a").last().attr("href")
            } else {
                null
            }
        }
    }
}