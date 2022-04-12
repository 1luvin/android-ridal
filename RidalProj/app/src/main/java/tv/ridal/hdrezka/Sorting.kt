package tv.ridal.hdrezka

import tv.ridal.utils.Locale
import kotlin.collections.HashMap

class Sorting
{
    companion object
    {
        const val url_base = "?filter="

        private var sortingUrls = HashMap<String, String>().apply {
            this[Locale.text(Locale.sorting_last)] = "last"
            this[Locale.text(Locale.sorting_popular)] = "popular"
            this[Locale.text(Locale.sorting_watching)] = "watching"
        }

        fun url(sortingKey: String) : String
        {
            var url = ""
            if ( sortingUrls.containsKey(sortingKey) ) {
                url += url_base
                url += sortingUrls[sortingKey]
            }

            return url
        }
    }
}